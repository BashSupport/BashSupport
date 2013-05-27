package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.FileInclusionManager;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.util.BashFunctions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;

import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jansorg
 * Date: 27.05.13
 * Time: 23:57
 * To change this template use File | Settings | File Templates.
 */
public class BashElementSharedImpl {
    static GlobalSearchScope getElementGlobalSearchScope(BashPsiElement element, Project project) {
        //fixme is this right?
        BashFile psiFile = (BashFile) element.getContainingFile();
        GlobalSearchScope currentFileScope = GlobalSearchScope.fileScope(psiFile);

        Set<PsiFile> includedFiles = FileInclusionManager.findIncludedFiles(psiFile, true, true);
        Collection<VirtualFile> files = Collections2.transform(includedFiles, BashFunctions.psiToVirtualFile());

        return currentFileScope.uniteWith(GlobalSearchScope.filesScope(project, files));
    }

    static SearchScope getElementUseScope(BashPsiElement element, Project project) {
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
}
