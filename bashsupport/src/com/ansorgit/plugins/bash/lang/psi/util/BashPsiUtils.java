/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPsiUtils.java, Class: BashPsiUtils
 * Last modified: 2013-05-12
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashBlock;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashSubshellCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: jansorg
 * Date: 04.08.2009
 * Time: 21:45:47
 */
public final class BashPsiUtils {
    private BashPsiUtils() {
    }

    /**
     * Finds the file context for a given element. If element is inside of an Bash file injection host (e.g. because the element is in an eval command)
     * then the host file is returned.
     *
     * @param element
     * @return The file on disk
     */
    public static PsiFile findFileContext(PsiElement element) {
        PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(element.getProject()).getInjectionHost(element);
        if (injectionHost != null && injectionHost.getContainingFile() instanceof BashFile) {
            return injectionHost.getContainingFile();
        }

        return element.getContainingFile();
    }

    /**
     * Returns the depth in blocks this element has in the tree.
     *
     * @param element The element to lookup
     * @return The depth measured in blocks, 0 if it's at the top level
     */
    public static int blockNestingLevel(PsiElement element) {
        int depth = 0;

        PsiElement current = findEnclosingBlock(element);
        while (current != null) {
            depth++;
            current = findEnclosingBlock(current);
        }

        return depth;
    }

    /**
     * Returns the broadest scope of the variable definition.
     *
     * @param startElement The element to check
     * @return The containing block or null
     */
    @Nullable
    public static BashFunctionDef findBroadestFunctionScope(PsiElement startElement) {
        BashFunctionDef lastValidScope = null;

        PsiElement element = startElement.getContext();
        while (element != null) {
            element = element.getContext();

            if (element == null) {
                return lastValidScope;
            }

            if (element instanceof BashFunctionDef) {
                lastValidScope = (BashFunctionDef) element;
            }
        }

        return null;
    }

    /**
     * Returns the broadest scope of the variable definition.
     *
     * @param varDef The element to check
     * @return The containing block or null
     */
    public static BashFunctionDef findNextVarDefFunctionDefScope(PsiElement varDef) {
        PsiElement element = varDef.getContext();
        while (element != null) {
            element = element.getContext();

            if (element instanceof BashFunctionDef) {
                return (BashFunctionDef) element;
            }
        }

        return null;
    }

    /**
     * Returns the next logical block which contains this element.
     *
     * @param element The element to check
     * @return The containing block or null
     */
    public static PsiElement findEnclosingBlock(PsiElement element) {
        while (element != null) {
            element = element.getContext();

            if (isValidContainer(element)) {
                return element;
            }
        }

        return null;
    }

    private static boolean isValidContainer(PsiElement element) {
        return element instanceof BashBlock || element instanceof BashFunctionDef || element instanceof BashFile;
    }

    public static int getElementLineNumber(PsiElement element) {
        FileViewProvider fileViewProvider = element.getContainingFile().getViewProvider();
        if (fileViewProvider.getDocument() != null) {
            return fileViewProvider.getDocument().getLineNumber(element.getTextOffset()) + 1;
        }

        return 0;
    }

    public static int getElementEndLineNumber(PsiElement element) {
        FileViewProvider fileViewProvider = element.getContainingFile().getViewProvider();
        if (fileViewProvider.getDocument() != null) {
            return fileViewProvider.getDocument().getLineNumber(element.getTextOffset() + element.getTextLength()) + 1;
        }

        return 0;
    }

    public static IElementType nodeType(PsiElement element) {
        ASTNode node = element.getNode();
        return node == null ? null : node.getElementType();
    }

    public static PsiElement findNextSibling(PsiElement start, IElementType ignoreType) {
        PsiElement current = start.getNextSibling();
        while (current != null) {
            if (ignoreType != nodeType(current)) {
                return current;
            }

            current = current.getNextSibling();
        }

        return null;
    }

    public static PsiElement findPreviousSibling(PsiElement start, IElementType ignoreType) {
        PsiElement current = start.getPrevSibling();
        while (current != null) {
            if (ignoreType != nodeType(current)) {
                return current;
            }

            current = current.getPrevSibling();
        }

        return null;
    }

    /**
     * Replaces the priginal element with the replacement.
     *
     * @param original    The original element which should be replaced.
     * @param replacement The new element
     * @return The replaces element. Depending on the context of the original element it either the original element or the replacement element.
     */
    public static PsiElement replaceElement(PsiElement original, PsiElement replacement) throws IncorrectOperationException {
        try {
            return original.replace(replacement);
        } catch (IncorrectOperationException e) {
            //failed, try another way
        } catch (UnsupportedOperationException e) {
            //failed, try another way
        }

        PsiElement parent = original.getParent();
        if (parent != null) {
            PsiElement inserted = parent.addBefore(replacement, original);
            original.delete();
            return inserted;
        } else {
            //last try, not optimal
            original.getNode().replaceAllChildrenToChildrenOf(replacement.getNode());
            return original;
        }
    }

    @Nullable
    public static TextRange rangeInParent(PsiElement parent, PsiElement child) {
        if (!parent.getTextRange().contains(child.getTextRange())) {
            return null;
        }

        return TextRange.from(child.getTextOffset() - parent.getTextOffset(), child.getTextLength());
    }

    public static boolean isStaticWordExpr(PsiElement child) {
        while (child != null) {
            if (child instanceof BashVar || child instanceof BashSubshellCommand) {
                return false;
            }

            //a string may contain other composed elements, e.g. "$a" contains a wrapped word which contains the var
            if (!isStaticWordExpr(child.getFirstChild())) {
                return false;
            }

            child = child.getNextSibling();
        }

        return true;
    }

    /**
     * This tree walkup method does continue even if a valid definition has been found on an more-inner level.
     * Bash is different in regard to the definitions, the most outer definitions count, not the most inner / the first one found.
     *
     * @param processor
     * @param entrance
     * @param maxScope
     * @param state
     * @return
     */
    public static boolean varResolveTreeWalkUp(@NotNull final PsiScopeProcessor processor,
                                               @NotNull final BashVar entrance,
                                               @Nullable final PsiElement maxScope,
                                               @NotNull final ResolveState state) {
        PsiElement prevParent = entrance;
        PsiElement scope = entrance;

        boolean hasResult = false;

        while (scope != null) {
            hasResult |= !scope.processDeclarations(processor, state, prevParent, entrance);

            if (scope == maxScope) break;

            prevParent = scope;
            scope = prevParent.getContext();
        }

        return !hasResult;
    }

    @Nullable
    public static PsiFile findIncludedFile(BashCommand bashCommand) {
        if (bashCommand instanceof BashIncludeCommand) {
            BashFileReference reference = ((BashIncludeCommand) bashCommand).getFileReference();

            if (reference != null) {
                return reference.findReferencedFile();
            }
        }

        return null;
    }

    /**
     * Returns the commands of file which include the other file.
     *
     * @param file
     * @param includedFile
     * @return The list of commands, may be empty but wont be null
     */
    public static List<BashCommand> findIncludeCommands(PsiFile file, final PsiFile includedFile) {
        final List<BashCommand> includeCommands = Lists.newLinkedList();

        BashVisitor collecingVisitor = new BashVisitor() {
            @Override
            public void visitIncludeCommand(BashIncludeCommand bashCommand) {
                if (includedFile.equals(findIncludedFile(bashCommand))) {
                    includeCommands.add(bashCommand);
                }
            }
        };

        visitRecursively(file, collecingVisitor);

        return includeCommands;
    }

    public static void visitRecursively(PsiElement element, BashVisitor visitor) {
        element.accept(visitor);

        // calling element.getChildren() is expensive,
        // better iterate over the chilren
        PsiElement child = element.getFirstChild();
        while (child != null) {
            if (child.getNode() instanceof CompositeElement) {
                visitRecursively(child, visitor);
            }

            child = child.getNextSibling();
        }
    }

    public static boolean hasContext(PsiElement element, PsiElement contextCandidate) {
        for (PsiElement ref = element; ref != null; ref = ref.getContext()) {
            if (ref == contextCandidate) {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidReferenceScope(PsiElement childCandidate, PsiElement variableDefinition) {
        final boolean sameFile = variableDefinition.getContainingFile().equals(childCandidate.getContainingFile());
        if (sameFile) {
            if (!isValidGlobalOffset(childCandidate, variableDefinition)) {
                return false;
            }
        } else {
            //we need to find the include command and check the offset
            //the include command must fullfil the same condition as the normal variable definition above:
            //either var use and definition are both in functions or it the use is invalid
            List<BashCommand> includeCommands = findIncludeCommands(childCandidate.getContainingFile(), variableDefinition.getContainingFile());

            //currently we only support global include commands
            for (BashCommand includeCommand : includeCommands) {
                if (!isValidGlobalOffset(childCandidate, includeCommand)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isValidGlobalOffset(PsiElement referenceElement, PsiElement definition) {
        if (definition.getTextOffset() > referenceElement.getTextOffset()) {
            if (isGlobal(referenceElement)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isGlobal(PsiElement element) {
        return findBroadestFunctionScope(element) == null;
    }

    public static List<PsiComment> findDocumentationElementComments(PsiElement element) {
        PsiElement command = findParent(element, BashCommand.class);
        if (command == null) {
            command = findParent(element, BashFunctionDef.class);
        }

        if (command == null) {
            return Collections.emptyList();
        }

        int previousLine = getElementLineNumber(element);

        PsiElement current = command.getPrevSibling();

        LinkedList<PsiComment> result = Lists.newLinkedList();

        while (current != null && current.getNode() != null && current.getNode().getElementType() == BashTokenTypes.LINE_FEED) {
            current = current.getPrevSibling();

            if (current instanceof PsiComment && BashPsiUtils.getElementEndLineNumber(current) + 1 == previousLine) {
                result.add(0, (PsiComment) current);
                previousLine = getElementLineNumber(current);
                current = current.getPrevSibling();
            } else {
                break;
            }
        }

        return result;
    }

    @Nullable
    public static <T extends PsiElement> T findParent(@Nullable PsiElement start, Class<T> parentType) {
        if (start == null) {
            return null;
        }

        for (PsiElement current = start.getParent(); current != null; current = current.getParent()) {
            if (parentType.isInstance(current)) {
                return (T) current;
            }
        }

        return null;
    }

    public static boolean hasParentOfType(PsiElement start, Class<? extends PsiElement> parentType, int maxSteps) {
        for (PsiElement current = start; current != null && maxSteps-- >= 0; current = current.getParent()) {
            if (parentType.isInstance(current)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isInjectedElement(@NotNull PsiElement element) {
        //fixme languageManager is probably expensive
        InjectedLanguageManager languageManager = InjectedLanguageManager.getInstance(element.getProject());
        return languageManager.isInjectedFragment(element.getContainingFile()) || hasInjectionHostParent(element);
    }

    private static boolean hasInjectionHostParent(PsiElement element) {
        return hasParentOfType(element, PsiLanguageInjectionHost.class, 10);
    }

    /**
     * Returns the start text offset of the element in the toplevel file, i.e the PsiFile which containing the real document. If an element
     * is injected then the outer file is returned.
     *
     * @param element The element to work on
     * @return The start text offset in the physical PsiFile, injected virtual PsiFiles are not used for text offset calculation
     */
    public static int getFileTextOffset(PsiElement element) {
        int offset = element.getTextOffset();
        if (isInjectedElement(element)) {
            //fixme languageManager is probably expensive
            InjectedLanguageManager languageManager = InjectedLanguageManager.getInstance(element.getProject());

            PsiLanguageInjectionHost injectionHost = languageManager.getInjectionHost(element);
            if (injectionHost != null) {
                offset += injectionHost.getTextOffset();
            }
        }

        return offset;
    }
}
