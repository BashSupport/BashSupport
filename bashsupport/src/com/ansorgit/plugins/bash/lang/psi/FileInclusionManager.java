package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.impl.FileManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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

    public static Set<PsiFile> findIncludedFiles(PsiFile file) {
        if (file instanceof BashFile) {
            return ((BashFile) file).findIncludedFiles(true, true);
        }

        return Collections.emptySet();
    }

    /**
     * Finds all files which include the given file.
     * The bash files of the module are checked if they include the file.
     *
     * @param project
     * @param file
     * @return
     */
    public static Set<BashFile> findIncludingFiles(Project project, PsiFile file) {
        //fixme this method is slow and should be replaced with an index lookup when available
        List<BashFile> allFiles = findAllFiles(project, file);

        Set<BashFile> includers = Sets.newHashSet();

        for (BashFile currentFile : allFiles) {
            Set<PsiFile> includedFiles = currentFile.findIncludedFiles(true, true);

            if (includedFiles.contains(file)) {
                includers.add(currentFile);
            }
        }

        return includers;
    }

    @NotNull
    private static List<BashFile> findAllFiles(Project project, @NotNull PsiFile file) {
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) {
            return Collections.emptyList();
        }

        List<BashFile> files = Lists.newLinkedList();

        Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
        findAllFiles(files, module, project);

        return files;
    }

    private static void findAllFiles(@NotNull List<BashFile> result, Module module, Project project) {
        final VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile root : contentRoots) {
            collectSubtree(result, root, psiManager);
        }
    }


    private static void collectSubtree(List<BashFile> result, VirtualFile child, PsiManager psiManager) {
        if (child.isDirectory()) {
            //directory
            for (VirtualFile file : child.getChildren()) {
                collectSubtree(result, file, psiManager);
            }
        } else {
            PsiFile psiFile = psiManager.findFile(child);
            if (psiFile instanceof BashFile) {
                result.add((BashFile) psiFile);
            }
        }
    }
}
