/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: FileInclusionManager.java, Class: FileInclusionManager
 * Last modified: 2011-07-17 20:06
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
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

    @NotNull
    public static Set<PsiFile> findIncludedFiles(@NotNull PsiFile file) {
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
    @NotNull
    public static Set<BashFile> findIncludingFiles(@NotNull Project project, @NotNull PsiFile file) {
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
    private static List<BashFile> findAllFiles(@NotNull Project project, @NotNull PsiFile file) {
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) {
            return Collections.emptyList();
        }

        List<BashFile> files = Lists.newLinkedList();

        Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
        if (module != null) {
            findAllFiles(files, module, project);
        }

        return files;
    }

    private static void findAllFiles(@NotNull List<BashFile> result, @NotNull Module module, @NotNull Project project) {
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
