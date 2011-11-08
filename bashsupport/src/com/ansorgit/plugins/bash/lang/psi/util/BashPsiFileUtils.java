/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPsiFileUtils.java, Class: BashPsiFileUtils
 * Last modified: 2011-07-17 20:00
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

package com.ansorgit.plugins.bash.lang.psi.util;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
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
        PsiDirectory startDirectory = start.getContainingFile().getContainingDirectory();
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
}
