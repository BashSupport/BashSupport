/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: VfsCompletionUtil.java, Class: VfsCompletionUtil
 * Last modified: 2009-12-04
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

package com.ansorgit.plugins.bash.util;

import com.google.common.collect.Lists;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Class which offers path name completion in the project's virtual file system.
 * <p/>
 * User: jansorg
 * Date: Dec 3, 2009
 * Time: 9:38:10 PM
 */
public class VfsCompletionUtil {
    private VfsCompletionUtil() {
    }

    /**
     * Collects all childs which conform to a subpath inside of the base directory.
     *
     * @param base           The base directory which is the base for relative paths
     * @param relativePrefix A partly path, e.g. "subdir/subdir/fileprefix" or just "subdi"
     * @return The list of paths relative to base which match the given prefix
     */
    //fixme make this work with relative parent dirs, e.g. ./../ot
    public static List<String> relativePathCompletion(PsiDirectory base, String relativePrefix) {
        return relativePathCompletion(base, ".", relativePrefix);
    }

    public static List<String> relativePathCompletion(PsiDirectory base, String shownBase, String relativePrefix) {
        String parentDirName = "";
        String fileName = "";

        //split the path
        int lastSlashPos = relativePrefix.lastIndexOf('/');
        if (lastSlashPos == -1) {
            fileName = relativePrefix;
        } else if (lastSlashPos < relativePrefix.length() - 1) {
            parentDirName = relativePrefix.substring(0, lastSlashPos);
            fileName = relativePrefix.substring(lastSlashPos + 1);
        } else {
            parentDirName = relativePrefix;
        }

        PsiDirectory parent = dirInSubtree(base, parentDirName);
        if (parent == null) {
            return Collections.emptyList();
        }

        List<PsiFile> psiFiles = matchingFiles(parent, fileName);
        String commonPrefix = base.getVirtualFile().getPath();

        List<String> result = Lists.newLinkedList();

        for (PsiFile f : psiFiles) {
            VirtualFile file = f.getVirtualFile();
            if (file == null) {
                continue;
            }

            String fullPath = file.getPath();
            if (fullPath.startsWith(commonPrefix)) {
                result.add("." + fullPath.substring(commonPrefix.length()));
            }
        }

        return result;
    }

    /**
     * Finds all files in the parent directory which conform to filenamePrefix.
     *
     * @param parent   The parent directory
     * @param fileName The filename prefix to check
     * @return The list of matching files. May be empty, won't be null.
     */
    public static List<PsiFile> matchingFiles(@NotNull PsiDirectory parent, @NotNull String fileName) {
        List<PsiFile> result = Lists.newLinkedList();

        for (PsiFile f : parent.getFiles()) {
            if (f.getName().startsWith(fileName)) {
                result.add(f);
            }
        }

        return result;
    }

    public static PsiDirectory dirInSubtree(PsiDirectory parent, String pathSpec) {
        if (pathSpec.isEmpty() && pathSpec.equals(".")) {
            return parent;
        }

        PsiDirectory current = parent;
        String[] paths = pathSpec.split("/");
        for (String p : paths) {
            if (p.equals(".")) {
                continue;
            }

            if (current == null) {
                break;
            }

            if (p.equals("..")) {
                current = current.getParentDirectory();
                continue;
            }

            PsiDirectory subdir = current.findSubdirectory(p);
            if (subdir == null) {
                return null;
            }

            current = subdir;
        }

        return current;
    }
}
