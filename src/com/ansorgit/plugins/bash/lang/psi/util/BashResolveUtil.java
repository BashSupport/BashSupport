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

import com.ansorgit.plugins.bash.lang.psi.api.*;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.loops.BashLoop;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.Keys;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarProcessor;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludeCommandIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashVarDefIndex;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.injected.editor.VirtualFileWindow;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class BashResolveUtil {
    private BashResolveUtil() {
    }

    public static GlobalSearchScope varDefSearchScope(BashVar reference, boolean withIncludedFiles) {
        PsiFile referenceFile = BashPsiUtils.findFileContext(reference);
        if (!withIncludedFiles) {
            return GlobalSearchScope.fileScope(referenceFile.getProject(), referenceFile.getVirtualFile());
        }

        Set<VirtualFile> result = Sets.newLinkedHashSet();
        result.add(referenceFile.getVirtualFile());

        int referenceFileOffset = BashPsiUtils.getFileTextOffset(reference);
        BashFunctionDef referenceFunctionContainer = BashPsiUtils.findNextVarDefFunctionDefScope(reference);

        for (BashIncludeCommand command : BashPsiUtils.findIncludeCommands(referenceFile, null)) {
            boolean includeIsInFunction = BashPsiUtils.findNextVarDefFunctionDefScope(command) != null;

            //either one of var or include command is in a function or the var is used after the include command
            if (referenceFunctionContainer != null || includeIsInFunction || (referenceFileOffset > BashPsiUtils.getFileTextEndOffset(command))) {
                BashFileReference fileReference = command.getFileReference();
                PsiFile includedFile = fileReference != null ? fileReference.findReferencedFile() : null;
                if (includedFile != null) {
                    result.add(includedFile.getVirtualFile());

                    //also, add all files included in the valid include command's file
                    for (PsiFile file : BashPsiUtils.findIncludedFiles(includedFile, true)) {
                        result.add(file.getVirtualFile());
                    }
                }
            }
        }

        return GlobalSearchScope.filesScope(referenceFile.getProject(), result);
    }


    /**
     * Iterate all variable definitions which apply to the given variable.
     * This includes redeclarations and the original definition it resolves to.
     *
     * @param reference       The variable to use as starting point
     * @param resultProcessor The function called with each of the located definitions
     */
    public static void walkVariableDefinitions(@NotNull BashVar reference, @NotNull Function<BashVarDef, Boolean> resultProcessor) {
        String varName = reference.getName();
        if (StringUtils.isBlank(varName)) {
            return;
        }

        BashVarProcessor processor = new BashVarProcessor(reference, varName, true, false, true);
        resolve(reference, false, processor);

        Collection<PsiElement> results = processor.getResults();
        if (results != null) {
            for (PsiElement result : results) {
                if (result instanceof BashVarDef) {
                    resultProcessor.apply((BashVarDef) result);
                }
            }
        }
    }

    public static PsiElement resolve(BashVar bashVar, boolean dumbMode, boolean preferNeighborhood) {
        if (bashVar == null || !bashVar.isPhysical()) {
            return null;
        }

        final String varName = bashVar.getReferenceName();
        if (varName == null) {
            return null;
        }

        return resolve(bashVar, dumbMode, new BashVarProcessor(bashVar, varName, true, preferNeighborhood, false));
    }

    public static PsiElement resolve(BashVar bashVar, boolean dumbMode, ResolveProcessor processor) {
        if (bashVar == null || !bashVar.isPhysical()) {
            return null;
        }

        final String varName = bashVar.getReferenceName();
        if (varName == null) {
            return null;
        }

        PsiFile psiFile = BashPsiUtils.findFileContext(bashVar);
        VirtualFile virtualFile = psiFile.getVirtualFile();

        String filePath = virtualFile != null ? virtualFile.getPath() : null;
        Project project = bashVar.getProject();

        ResolveState resolveState = ResolveState.initial();

        GlobalSearchScope fileScope = GlobalSearchScope.fileScope(psiFile);

        Collection<BashVarDef> varDefs;
        if (dumbMode || isScratchFile(virtualFile) || isNotIndexedFile(project, virtualFile)) {
            varDefs = PsiTreeUtil.collectElementsOfType(psiFile, BashVarDef.class);
        } else {
            varDefs = StubIndex.getElements(BashVarDefIndex.KEY, varName, project, fileScope, BashVarDef.class);
        }

        for (BashVarDef varDef : varDefs) {
            ProgressManager.checkCanceled();

            processor.execute(varDef, resolveState);
        }

        if (!dumbMode && filePath != null) {
            Collection<BashIncludeCommand> includeCommands = StubIndex.getElements(BashIncludeCommandIndex.KEY, filePath, project, fileScope, BashIncludeCommand.class);
            if (!includeCommands.isEmpty()) {
                boolean varIsInFunction = BashPsiUtils.findNextVarDefFunctionDefScope(bashVar) != null;

                for (BashIncludeCommand command : includeCommands) {
                    ProgressManager.checkCanceled();

                    boolean includeIsInFunction = BashPsiUtils.findNextVarDefFunctionDefScope(command) != null;

                    //either one of var or include command is in a function or the var is used after the include command
                    if (varIsInFunction || includeIsInFunction || (BashPsiUtils.getFileTextOffset(bashVar) > BashPsiUtils.getFileTextEndOffset(command))) {
                        try {
                            resolveState = resolveState.put(Keys.resolvingIncludeCommand, command);

                            command.processDeclarations(processor, resolveState, command, bashVar);
                        } finally {
                            resolveState = resolveState.put(Keys.resolvingIncludeCommand, null);
                        }
                    }
                }
            }
        }

        processor.prepareResults();

        return processor.getBestResult(false, bashVar);
    }

    public static boolean isNotIndexedFile(@NonNls Project project, @Nullable VirtualFile virtualFile) {
        return virtualFile == null
                || virtualFile instanceof VirtualFileWindow
                || !FileIndexFacade.getInstance(project).isInContent(virtualFile);
    }

    public static boolean processContainerDeclarations(BashPsiElement thisElement, @NotNull final PsiScopeProcessor processor, @NotNull final ResolveState state, final PsiElement lastParent, @NotNull final PsiElement place) {
        if (thisElement == lastParent) {
            return true;
        }

        if (!processor.execute(thisElement, state)) {
            return false;
        }

        //process the current's elements children from first until lastParent is reached, a definition has to be before the use
        List<PsiElement> functions = Lists.newLinkedList();

        //fixme use stubs
        for (PsiElement child = thisElement.getFirstChild(); child != null && child != lastParent; child = child.getNextSibling()) {
            if (child instanceof BashFunctionDef) {
                functions.add(child);
            } else if (!child.processDeclarations(processor, state, lastParent, place)) {
                return false;
            }
        }

        for (PsiElement function : functions) {
            if (!function.processDeclarations(processor, state, lastParent, place)) {
                return false;
            }
        }

        //fixme this is very slow atm
        if (lastParent != null && lastParent.getParent().isEquivalentTo(thisElement) && BashPsiUtils.findNextVarDefFunctionDefScope(place) != null) {
            for (PsiElement sibling = lastParent.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
                if (!sibling.processDeclarations(processor, state, null, place)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isScratchFile(@Nullable PsiFile file) {
        return file != null && isScratchFile(file.getVirtualFile());
    }

    public static boolean isScratchFile(@Nullable VirtualFile file) {
        return file != null && ScratchFileService.getInstance().getRootType(file) != null;
    }

    /**
     * @return true if the definition of this variable is not child of a conditional command or loop.
     */
    public static boolean hasStaticVarDefPath(BashVar bashVar) {
        BashReference reference = bashVar.getNeighborhoodReference();
        if (reference == null) {
            return false;
        }

        PsiElement closestDef = reference.resolve();
        if (closestDef == null) {
            return false;
        }

        // if the closest def is in a different def scope, then we can't handle that
        // (e.g. var is top-level, def is in a function or var is in a function and def in another function, etc.)
        BashFunctionDef varScope = BashPsiUtils.findNextVarDefFunctionDefScope(bashVar);
        BashFunctionDef defScope = BashPsiUtils.findNextVarDefFunctionDefScope(closestDef);
        if (varScope == null && defScope != null) {
            return false;
        }

        // we can't handle different functions as scope
        if (varScope != null && !varScope.isEquivalentTo(defScope)) {
            return false;
        }

        // atm we can't handle different files
        PsiFile psiFile = bashVar.getContainingFile();
        if (varScope == null && !psiFile.isEquivalentTo(closestDef.getContainingFile())) {
            return false;
        }

        Collection<BashVarDef> allDefs = StubIndex.getElements(BashVarDefIndex.KEY, bashVar.getReferenceName(), bashVar.getProject(), GlobalSearchScope.fileScope(psiFile), BashVarDef.class);
        for (BashVarDef candidateDef : allDefs) {
            ProgressManager.checkCanceled();

            // skip var defs which are not in our own def scope
            BashFunctionDef scope = BashPsiUtils.findNextVarDefFunctionDefScope(candidateDef);
            if (varScope != null && !varScope.isEquivalentTo(scope)) {
                continue;
            }

            // it's not a static path if the var def is in a conditional block or loop and if our var is not
            PsiElement parent = PsiTreeUtil.findFirstParent(candidateDef, psi -> psi instanceof BashConditionalBlock || psi instanceof BashLoop);
            if (parent != null && !PsiTreeUtil.isAncestor(parent, bashVar, true)) {
                return false;
            }
        }

        return true;
    }
}
