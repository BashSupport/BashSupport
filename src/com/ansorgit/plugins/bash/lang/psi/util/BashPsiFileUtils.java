/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPsiFileUtils.java, Class: BashPsiFileUtils
 * Last modified: 2011-07-17 20:00
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.psi.util;

import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.apache.xml.resolver.helpers.FileURL;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * User: jansorg
 * Date: Nov 2, 2009
 * Time: 8:50:30 PM
 */
public class BashPsiFileUtils {
    /**
     * Takes an existing psi file and tries to find another file relative to the first.
     * The file path is given as a relative path.
     *
     * @param start        The existing psi file
     * @param relativePath The relative path as a string
     * @return The psi file or null if nothing has been found
     */
    @Nullable
    public static PsiFile findRelativeFile(PsiFile start, String relativePath) {
        PsiDirectory startDirectory = BashPsiUtils.findFileContext(start).getContainingDirectory();
        if (startDirectory == null || StringUtil.isEmptyOrSpaces(relativePath)) {
            return null;
        }

        //fixme handle escaped / chars!
        PsiDirectory currentDir = startDirectory;

        List<String> parts = StringUtil.split(relativePath, "/");
        String filePart = parts.size() > 0 ? parts.get(parts.size() - 1) : "";

        for (int i = 0, partsLength = parts.size() - 1; (i < partsLength) && (currentDir != null); i++) {
            String part = parts.get(i);

            if (".".equals(part)) {
                //ignore this
            } else if ("..".equals(part)) {
                currentDir = currentDir.getParentDirectory();
            } else {
                currentDir = currentDir.findSubdirectory(part);
            }
        }

        if (currentDir != null) {
            return currentDir.findFile(filePart);
        }

        return null;
    }

    @Nullable
    public static String findRelativeFilePath(PsiFile base, PsiFile targetFile) {
        PsiFile currentFile = BashPsiUtils.findFileContext(base);
        VirtualFile baseVirtualFile = currentFile.getVirtualFile();
        if (!(baseVirtualFile.getFileSystem() instanceof LocalFileSystem)) {
            throw new IncorrectOperationException("Can not rename file refeferences in non-local files");
        }

        VirtualFile targetVirtualFile = BashPsiUtils.findFileContext(targetFile).getVirtualFile();
        if (!(targetVirtualFile.getFileSystem() instanceof LocalFileSystem)) {
            throw new IncorrectOperationException("Can not bind to non-local files");
        }

        VirtualFile baseParent = baseVirtualFile.getParent();
        VirtualFile targetParent = targetVirtualFile.getParent();

        if (baseParent == null || targetParent == null){
            throw new IllegalStateException("parent directories not found");
        }

        String baseDirPath = baseParent.getPath();
        String targetDirPath = targetParent.getPath();
        String targetRelativePath = FileUtilRt.getRelativePath(baseDirPath, targetDirPath, '/', true);

        if (".".equals(targetRelativePath)) {
            //same parent dir
            return targetVirtualFile.getName();
        }

        return targetRelativePath + '/' + targetVirtualFile.getName();
    }
}
