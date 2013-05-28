package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.FileInclusionManager;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.util.BashFunctions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class BashElementSharedImpl {
    public static GlobalSearchScope getElementGlobalSearchScope(BashPsiElement element, Project project) {
        //fixme is this right?
        BashFile psiFile = (BashFile) element.getContainingFile();
        GlobalSearchScope currentFileScope = GlobalSearchScope.fileScope(psiFile);

        Set<PsiFile> includedFiles = FileInclusionManager.findIncludedFiles(psiFile, true, true);
        Collection<VirtualFile> files = Collections2.transform(includedFiles, BashFunctions.psiToVirtualFile());

        return currentFileScope.uniteWith(GlobalSearchScope.filesScope(project, files));
    }

    public static SearchScope getElementUseScope(BashPsiElement element, Project project) {
        //all files which include this element's file belong to the requested scope

        //fixme can this be optimized?

        PsiFile currentFile = element.getContainingFile();
        Set<BashFile> includers = FileInclusionManager.findIncluders(project, currentFile);
        Set<PsiFile> included = FileInclusionManager.findIncludedFiles(currentFile, true, true);

        if (includers.isEmpty() && included.isEmpty()) {
            //we should return a local search scope if we only have local references
            //not return a local scope then inline renaming is not possible
            return new LocalSearchScope(currentFile);
        }

        Collection<VirtualFile> virtualFiles = Collections2.transform(Sets.union(included, includers), BashFunctions.psiToVirtualFile());
        return GlobalSearchScope.fileScope(currentFile).union(GlobalSearchScope.filesScope(project, virtualFiles));
    }

    public static boolean walkDefinitionScope(PsiElement thisElement, @NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

        //walk the tree from top to bottom because the first definition has higher priority and should still be processed
        //if a later definition masks it

        PsiElement child = thisElement.getFirstChild();
        if (child == lastParent) {
            return true;
        }

        while (child != null) {
            if (!child.processDeclarations(processor, state, null, place)) {
                return false;
            }

            if (child == lastParent) {
                break;
            }

            child = child.getNextSibling();
        }

        // If the last processed child is the parent of the place element check if we need to process
        // the elements after the element
        // In certain cases the resolving has to continue below the initial place, e.g.
        // function x() {
        //    function y() {
        //        echo $a
        //    }
        //
        //    a=
        // }

        if (child != null) {
            PsiElement functionContainer = BashPsiUtils.findParent(child.getNextSibling(), BashFunctionDef.class);
            if (functionContainer != null && functionContainer != thisElement) {
                //process the siblings after the parent of place
                while (child != null) {
                    if (!child.processDeclarations(processor, state, null, place)) {
                        return false;
                    }

                    child = child.getNextSibling();
                }
            }
        }

        return true;
    }
}
