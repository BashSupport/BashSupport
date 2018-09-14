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

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.util.text.StringUtilRt;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author jansorg
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
        if (baseParent == null || targetParent == null) {
            throw new IllegalStateException("parent directories not found");
        }

        char separator = '/';

        String baseDirPath = ensureEnds(baseParent.getPath(), separator);
        String targetDirPath = ensureEnds(targetParent.getPath(), separator);

        String targetRelativePath = FileUtilRt.getRelativePath(baseDirPath, targetDirPath, separator, true);
        if (targetRelativePath == null) {
            return null;
        }

        if (".".equals(targetRelativePath)) {
            //same parent dir
            return targetVirtualFile.getName();
        }

        return ensureEnds(targetRelativePath, separator) + targetVirtualFile.getName();
    }


    public static boolean isSpecialBashFile(String name) {
        for (String bashSpecialFileName : BashFileType.BASH_SPECIAL_FILES) {
            if (bashSpecialFileName.equals(name)) {
                return true;
            }
        }
        return false;
    }


    private static String ensureEnds(@NotNull String s, final char endsWith) {
        return StringUtilRt.endsWithChar(s, endsWith) ? s : s + endsWith;
    }
}
