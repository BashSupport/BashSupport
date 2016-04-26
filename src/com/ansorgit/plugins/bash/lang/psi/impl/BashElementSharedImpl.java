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

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.jetbrains.PsiScopesUtil;
import com.ansorgit.plugins.bash.lang.psi.FileInclusionManager;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashCommandNameIndex;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public final class BashElementSharedImpl {
    private BashElementSharedImpl() {
    }

    public static GlobalSearchScope getElementGlobalSearchScope(BashPsiElement element, Project project) {
        PsiFile psiFile = BashPsiUtils.findFileContext(element);
        GlobalSearchScope currentFileScope = GlobalSearchScope.fileScope(psiFile);

        Set<PsiFile> includedFiles = FileInclusionManager.findIncludedFiles(psiFile, true, true);
        Collection<VirtualFile> files = Collections2.transform(includedFiles, psiToVirtualFile());

        return currentFileScope.uniteWith(GlobalSearchScope.filesScope(project, files));
    }

    public static SearchScope getElementUseScope(BashPsiElement element, Project project) {
        //all files which include this element's file belong to the requested scope
        //bash files can call other bash files, thus the scope needs to be the module scope at minumum
        //fixme can this be optimized?
        PsiFile currentFile = BashPsiUtils.findFileContext(element);
        if (currentFile == null) {
            //no other fallback possible here
            return GlobalSearchScope.projectScope(project);
        }

        Set<BashFile> includers = FileInclusionManager.findIncluders(project, currentFile);
        Set<PsiFile> included = FileInclusionManager.findIncludedFiles(currentFile, true, true);

        //find all files which reference the source file
        Set<PsiFile> referencingScriptFiles = Sets.newLinkedHashSet();
        if (element instanceof BashFile) {
            String searchedName = ((BashFile) element).getName();
            if (searchedName != null) {
                Collection<BashCommand> commands = StubIndex.getElements(
                        BashCommandNameIndex.KEY,
                        searchedName,
                        project,
                        GlobalSearchScope.projectScope(project), //module scope isn't working as expected because it doesn't include non-src dirs
                        BashCommand.class);
                if (commands != null) {
                    for (BashCommand command : commands) {
                        referencingScriptFiles.add(BashPsiUtils.findFileContext(command));
                    }
                }
            }
        }

        if (includers.isEmpty() && included.isEmpty() && referencingScriptFiles.isEmpty()) {
            //we should return a local search scope if we only have local references
            //not return a local scope because then inline renaming is not possible
            return GlobalSearchScope.fileScope(currentFile);
        }

        //fixme improve this
        Set<PsiFile> union = Sets.newLinkedHashSet();
        union.addAll(included);
        union.addAll(includers);
        union.addAll(referencingScriptFiles);

        Collection<VirtualFile> virtualFiles = Collections2.transform(union, psiToVirtualFile());
        return GlobalSearchScope.fileScope(currentFile).union(GlobalSearchScope.filesScope(project, virtualFiles));
    }

    public static boolean walkDefinitionScope(PsiElement thisElement, @NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return PsiScopesUtil.walkChildrenScopes(thisElement, processor, state, lastParent, place);
    }

    private static Function<? super PsiFile, VirtualFile> psiToVirtualFile() {
        return new Function<PsiFile, VirtualFile>() {
            public VirtualFile apply(PsiFile psiFile) {
                return psiFile.getVirtualFile();
            }
        };
    }
}
