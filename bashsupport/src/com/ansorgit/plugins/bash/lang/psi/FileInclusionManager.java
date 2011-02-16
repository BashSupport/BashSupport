package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.util.List;
import java.util.Set;

/**
 * User: jansorg
 * Date: 06.02.11
 * Time: 20:36
 */
public class FileInclusionManager {
    private FileInclusionManager() {

    }

    /**
     * @param project
     * @param file
     * @return
     */
    public static Set<PsiFile> findIncludingFiles(Project project, PsiFile file) {
        //fixme this method is slow and should be replaces with an index lookup when available
        List<PsiFile> allFiles = findAllFiles(project);

        Set<PsiFile> includers = Sets.newHashSet();

        for (PsiFile currentFile : allFiles) {
            if (currentFile instanceof BashFile) {
                BashFile bashFile = (BashFile) currentFile;
                Set<PsiFile> includedFiles = bashFile.findIncludedFiles(true, true);

                if (includedFiles.contains(file)) {
                    includers.add(currentFile);
                }
            }
        }

        return includers;
    }

    private static List<PsiFile> findAllFiles(Project project) {
        List<PsiFile> files = Lists.newLinkedList();

        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            findAllFiles(files, module, project);
        }

        return files;
    }

    private static void findAllFiles(List<PsiFile> result, Module module, Project project) {
        //final ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        final VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile root : contentRoots) {
            collectSubtree(result, root, psiManager);
        }
    }

    private static void collectSubtree(List<PsiFile> result, VirtualFile child, PsiManager psiManager) {
        if (!child.isDirectory()) {
            PsiFile psiFile = psiManager.findFile(child);
            if (psiFile instanceof BashFile) {
                result.add(psiFile);
            }
        } else {
            for (VirtualFile file : child.getChildren()) {
                collectSubtree(result, file, psiManager);
            }
        }
    }


}
