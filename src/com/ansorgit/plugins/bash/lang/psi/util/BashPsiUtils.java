/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
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
import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashSubshellCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.loops.BashFor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.eval.BashEvalBlock;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludeCommandIndex;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author jansorg
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

        PsiElement element = PsiTreeUtil.getStubOrPsiParent(startElement);
        while (element != null) {
            if (element instanceof BashFunctionDef) {
                lastValidScope = (BashFunctionDef) element;
            }

            element = PsiTreeUtil.getStubOrPsiParent(element);
            if (element == null) {
                return lastValidScope;
            }
        }

        return null;
    }

    /**
     * Returns the narrowest scope of the variable definition.
     *
     * @param varDef The element to check
     * @return The containing block or null
     */
    public static BashFunctionDef findNextVarDefFunctionDefScope(PsiElement varDef) {
        PsiElement element = PsiTreeUtil.getStubOrPsiParent(varDef);
        while (element != null) {
            if (element instanceof BashFunctionDef) {
                return (BashFunctionDef) element;
            }

            element = PsiTreeUtil.getStubOrPsiParent(element);
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
            element = PsiTreeUtil.getStubOrPsiParent(element);

            if (isValidContainer(element)) {
                return element;
            }
        }

        return null;
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

    public static PsiElement findNextSibling(PsiElement start, IElementType ignoreType) {
        if (start == null) {
            return null;
        }

        PsiElement current = start.getNextSibling();
        while (current != null) {
            if (ignoreType != PsiUtilCore.getElementType(current)) {
                return current;
            }

            current = current.getNextSibling();
        }

        return null;
    }

    public static PsiElement findPreviousSibling(PsiElement start, IElementType ignoreType) {
        if (start == null) {
            return null;
        }

        PsiElement current = start.getPrevSibling();
        while (current != null) {
            if (ignoreType != PsiUtilCore.getElementType(current)) {
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
    public static <T extends PsiElement> T replaceElement(T original, PsiElement replacement) throws IncorrectOperationException {
        try {
            return (T) original.replace(replacement);
        } catch (IncorrectOperationException e) {
            //failed, try another way
        } catch (UnsupportedOperationException e) {
            //failed, try another way
        }

        PsiElement parent = original.getParent();
        if (parent != null) {
            PsiElement inserted = parent.addBefore(replacement, original);
            original.delete();
            return (T) inserted;
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

            if (scope == maxScope) {
                break;
            }

            prevParent = scope;
            scope = PsiTreeUtil.getStubOrPsiParent(prevParent);
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
     * @return The list of files which are included in the first file
     */
    public static Set<PsiFile> findIncludedFiles(PsiFile file, boolean followNestedFiles) {
        Set<PsiFile> files = Sets.newLinkedHashSet();

        collectIncludedFiles(file, files, followNestedFiles);

        return files;
    }

    public static void collectIncludedFiles(PsiFile file, Set<PsiFile> files, boolean followNestedFiles) {
        String filePath = file.getVirtualFile().getPath();

        Collection<BashIncludeCommand> commands = StubIndex.getElements(BashIncludeCommandIndex.KEY, filePath, file.getProject(), GlobalSearchScope.fileScope(file), BashIncludeCommand.class);
        for (BashIncludeCommand command : commands) {
            PsiFile includedFile = findIncludedFile(command);
            if (includedFile != null) {
                boolean followFile = followNestedFiles && !files.contains(includedFile);
                files.add(includedFile);

                if (followFile) {
                    collectIncludedFiles(includedFile, files, true);
                }
            }
        }
    }

    /**
     * Returns the commands of file which include the other file.
     *
     * @param file
     * @param filterByFile
     * @return The list of commands, may be empty but wont be null
     */
    public static List<BashIncludeCommand> findIncludeCommands(PsiFile file, @Nullable final PsiFile filterByFile) {
        String filePath = file.getVirtualFile().getPath();

        List<BashIncludeCommand> result = Lists.newLinkedList();

        Collection<BashIncludeCommand> commands = StubIndex.getElements(BashIncludeCommandIndex.KEY, filePath, file.getProject(), GlobalSearchScope.fileScope(file), BashIncludeCommand.class);
        for (BashIncludeCommand command : commands) {
            if (filterByFile == null || filterByFile.equals(findIncludedFile(command))) {
                result.add(command);
            }
        }

        return result;
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

    public static boolean isValidReferenceScope(PsiElement referenceToDefCandidate, PsiElement variableDefinition) {
        PsiFile definitionFile = findFileContext(variableDefinition);
        PsiFile referenceFile = findFileContext(referenceToDefCandidate);

        boolean sameFile = definitionFile.equals(referenceFile);

        if (sameFile) {
            return isValidGlobalOffset(referenceToDefCandidate, variableDefinition);
        }

        //we need to find the include command and check the offset
        //the include command must fullfil the same condition as the normal variable definition above:
        //either var use and definition are both in functions or it the use is invalid

        List<BashIncludeCommand> includeCommands = findIncludeCommands(referenceFile, definitionFile);

        //currently we only support global include commands
        for (BashCommand includeCommand : includeCommands) {
            if (!isValidGlobalOffset(referenceToDefCandidate, includeCommand)) {
                return false;
            }
        }

        return true;
    }

    public static List<PsiComment> findDocumentationElementComments(PsiElement element) {
        PsiElement command = findStubParent(element, BashCommand.class);
        if (command == null) {
            command = findStubParent(element, BashFunctionDef.class);
        }

        if (command == null) {
            return Collections.emptyList();
        }

        int previousLine = getElementLineNumber(element);

        PsiElement current = command.getPrevSibling();

        List<PsiComment> result = Lists.newLinkedList();

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
    public static <T extends PsiElement> T findStubParent(@Nullable PsiElement start, Class<T> parentType) {
        if (start == null) {
            return null;
        }

        Class<? extends PsiElement> breakPoint = PsiFile.class;

        for (PsiElement current = start; current != null; current = PsiTreeUtil.getStubOrPsiParent(current)) {
            if (parentType.isInstance(current)) {
                return (T) current;
            }

            if (breakPoint.isInstance(current)) {
                return null;
            }
        }

        return null;
    }

    @Nullable
    public static <T extends PsiElement> T findParent(@Nullable PsiElement start, Class<T> parentType) {
        return findParent(start, parentType, PsiFile.class);
    }

    @Nullable
    public static <T extends PsiElement> T findParent(@Nullable PsiElement start, Class<T> parentType, Class<? extends PsiElement> breakPoint) {
        if (start == null) {
            return null;
        }

        for (PsiElement current = start; current != null; current = current.getParent()) {
            if (parentType.isInstance(current)) {
                return (T) current;
            }

            if (breakPoint != null && breakPoint.isInstance(current)) {
                return null;
            }
        }

        return null;
    }

    public static boolean hasParentOfType(PsiElement start, Class<? extends PsiElement> parentType, int maxSteps) {
        return hasParentOfType(start, parentType, maxSteps, PsiFile.class);
    }

    public static boolean isCommandParameterWord(PsiElement start) {
        BashCommand command = PsiTreeUtil.getParentOfType(start, BashCommand.class);
        if (command == null) {
            return false;
        }

        BashWord word = PsiTreeUtil.getParentOfType(start, BashWord.class);

        return word != null && PsiTreeUtil.getPrevSiblingOfType(word, BashGenericCommand.class) != null;

    }

    public static boolean hasParentOfType(PsiElement start, Class<? extends PsiElement> parentType, int maxSteps, Class<? extends PsiElement> breakPoint) {
        for (PsiElement current = start; current != null && maxSteps-- >= 0; current = current.getParent()) {
            if (parentType.isInstance(current)) {
                return true;
            }

            if (breakPoint != null && breakPoint.isInstance(current)) {
                return false;
            }
        }

        return false;
    }

    public static boolean isInjectedElement(@NotNull PsiElement element) {
        InjectedLanguageManager languageManager = InjectedLanguageManager.getInstance(element.getProject());
        return languageManager.isInjectedFragment(element.getContainingFile()) || hasInjectionHostParent(element);
    }

    /**
     * Returns the start text offset of the element in the toplevel file, i.e the PsiFile which containing the real document. If an element
     * is injected then the outer file is returned.
     *
     * @param element The element to work on
     * @return The start text offset in the physical PsiFile, injected virtual PsiFiles are not used for text offset calculation
     */
    public static int getFileTextOffset(@NotNull PsiElement element) {
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

    public static int getFileTextEndOffset(@NotNull PsiElement element) {
        return getFileTextOffset(element) + element.getTextLength();
    }

    public static TextRange getTextRangeInFile(PsiElement element) {
        int offset = getFileTextOffset(element);

        return TextRange.from(offset, element.getTextLength());
    }

    /**
     * Returns the deepest nested ast node which still covers the same part of the file as the parent node. Happens if a single leaf node is
     * contained in several composite parent nodes of the same range, e.g. a var in a combined word.
     *
     * @param parent The element to use as the startin point
     * @return The deepest node inside of parent which covers the same range or (if none exists) the input element
     */
    @NotNull
    public static ASTNode getDeepestEquivalent(ASTNode parent) {
        ASTNode element = parent;
        while (element.getFirstChildNode() != null && element.getFirstChildNode() == element.getLastChildNode() && element.getTextRange().equals(parent.getTextRange())) {
            element = element.getFirstChildNode();
        }

        return element;
    }

    @Nullable
    public static ASTNode findEquivalentParent(@NotNull ASTNode node, @Nullable IElementType stopAt) {
        TextRange sourceRange = node.getTextRange();

        ASTNode current = node;
        while (true) {
            ASTNode parent = current.getTreeParent();
            if (parent == null || !parent.getTextRange().equals(sourceRange)) {
                return stopAt != null && current.getElementType() != stopAt ? null : current;
            }

            current = parent;
            if (stopAt == null || stopAt.equals(current.getElementType())) {
                return current;
            }
        }
    }

    @Nullable
    public static PsiElement findEquivalentParent(@NotNull PsiElement node, @Nullable IElementType stopAt) {
        ASTNode parent = findEquivalentParent(node.getNode(), stopAt);
        if (parent != null) {
            return parent.getPsi();
        }
        return null;
    }

    @Nullable
    public static PsiReference selfReference(PsiElement element) {
        ElementManipulator<PsiElement> manipulator = ElementManipulators.getManipulator(element);
        if (manipulator == null) {
            return null;
        }

        return new PsiReferenceBase.Immediate<PsiElement>(element, manipulator.getRangeInElement(element), true, element);
    }

    public static boolean isSingleChildParent(PsiElement psi) {
        if (psi == null) {
            return false;
        }

        ASTNode child = psi.getNode();
        return child.getTreePrev() == null && child.getTreeNext() == null;
    }

    public static boolean isSingleChildParent(PsiElement psi, @NotNull IElementType childType) {
        if (psi == null) {
            return false;
        }

        ASTNode child = getDeepestEquivalent(psi.getNode());
        return child.getTreePrev() == null && child.getTreeNext() == null && (child.getElementType() == childType);
    }

    public static boolean isSingleChildParent(PsiElement psi, @NotNull Class<? extends PsiElement> childType) {
        if (psi == null) {
            return false;
        }

        ASTNode child = getDeepestEquivalent(psi.getNode());
        PsiElement childPsi = child.getPsi();

        return childType.isInstance(childPsi);
    }

    public static boolean isInEvalBlock(PsiElement element) {
        return findParent(element, BashEvalBlock.class) != null;
    }

    private static boolean isValidContainer(PsiElement element) {
        if (element instanceof BashFor) {
            // variables defined in a for loop are also visiable afterwards
            return false;
        }

        return element instanceof BashBlock || element instanceof BashFunctionDef || element instanceof BashFile;
    }

    /**
     * Checks whether the given definition is a valid definition for the referenceElement.
     * If both have the same definition context, then the text offsets are compared.
     * If the definition contexts are different functions then the definition is valid.
     * A global reference to a definition in a function is checked by text offset.
     * A reference in a function to a global definition is also checked by reference.
     *
     * @param referenceElement The element checked
     * @param definition       The definition checked
     * @return True if definition may be a valid definition for the reference
     */
    private static boolean isValidGlobalOffset(PsiElement referenceElement, PsiElement definition) {
        BashFunctionDef refScope = findNextVarDefFunctionDefScope(referenceElement);
        BashFunctionDef defScope = findNextVarDefFunctionDefScope(definition);

        int refOffset = referenceElement.getTextOffset();
        int defOffset = definition.getTextOffset();

        //both global or both in the same function
        if (refScope == defScope || (refScope != null && refScope.isEquivalentTo(defScope))) {
            //both may be null or a function scope
            return refOffset > defOffset;
        }

        //ref and def are in different functions
        if (refScope != null && defScope != null) {
            return true;
        }

        //def global: ref function must be after the definition context
        //ref global: ref must be after the definition context

        return refOffset > defOffset;
    }

    private static boolean hasInjectionHostParent(PsiElement element) {
        return hasParentOfType(element, PsiLanguageInjectionHost.class, 10);
    }
}
