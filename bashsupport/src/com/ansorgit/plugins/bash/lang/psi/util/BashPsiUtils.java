/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPsiUtils.java, Class: BashPsiUtils
 * Last modified: 2010-07-12
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

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashBlock;
import com.ansorgit.plugins.bash.lang.psi.api.BashCharSequence;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashSubshellCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * User: jansorg
 * Date: 04.08.2009
 * Time: 21:45:47
 */
public class BashPsiUtils {
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
     * @param varDef The element to check
     * @return The containing block or null
     */
    public static BashFunctionDef findBroadestVarDefFunctionDefScope(PsiElement varDef) {
        BashFunctionDef lastValidScope = null;

        PsiElement element = varDef.getContext();
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

    public static boolean processChildDeclarations(PsiElement parentContainer, PsiScopeProcessor processor, ResolveState resolveState, PsiElement lastParent, PsiElement place) {
        boolean hasResult = false;

        if (parentContainer.equals(lastParent)) {
            return true;
        }

        if (!processor.execute(parentContainer, resolveState)) {
            return false;
        }

        PsiElement lastChild = parentContainer;
        PsiElement child = parentContainer.getFirstChild();
        while (child != null) {
            if (!child.equals(lastParent)) {
                hasResult |= !child.processDeclarations(processor, resolveState, lastChild, place);
            }

            lastChild = child;
            child = child.getNextSibling();
        }

        return !hasResult;
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
    public static String findIncludedFilename(BashCommand bashCommand) {
        List<BashPsiElement> params = bashCommand.parameters();

        if (params.size() == 1) {
            BashPsiElement firstParam = params.get(0);

            if (firstParam instanceof BashCharSequence) {
                return ((BashCharSequence) firstParam).getUnwrappedCharSequence();
            }
        }

        return null;
    }

    @Nullable
    public static BashFile findIncludedFile(BashCommand bashCommand) {
        String filename = findIncludedFilename(bashCommand);
        if (filename != null) {
            PsiFile containingFile = bashCommand.getContainingFile();
            return (BashFile) BashPsiFileUtils.findRelativeFile(containingFile, filename);
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
            public void visitIncludeCommand(BashCommand bashCommand) {
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

        for (PsiElement child : element.getChildren()) {
            visitRecursively(child, visitor);
        }
    }

    public static boolean isValidReference(PsiElement childCandidate, PsiElement variableDefinition) {
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

    private static boolean isValidGlobalOffset(PsiElement childCandidate, PsiElement reference) {
        if (reference.getTextOffset() > childCandidate.getTextOffset()) {
            if (isGlobal(reference) && isGlobal(childCandidate)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isGlobal(PsiElement element) {
        return findBroadestVarDefFunctionDefScope(element) == null;
    }
}
