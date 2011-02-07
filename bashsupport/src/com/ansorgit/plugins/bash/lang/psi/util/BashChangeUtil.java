/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashChangeUtil.java, Class: BashChangeUtil
 * Last modified: 2010-04-21
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

package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 16.04.2009
 * Time: 16:48:49
 *
 * @author Joachim Ansorg
 */
public class BashChangeUtil {
    private static final String TEMP_FILE_NAME = "__.sh";

    @NotNull
    private static PsiFile createFileFromText(@NotNull final Project project, @NotNull final String name, @NotNull final FileType fileType, @NotNull final String text) {
        return PsiFileFactory.getInstance(project).createFileFromText(name, fileType, text);
    }

    public static PsiFile createDummyBashFile(Project project, String text) {
        return createFileFromText(project, TEMP_FILE_NAME, BashFileType.BASH_FILE_TYPE, text);
    }

    public static PsiElement createSymbol(Project project, String name) {
        final PsiElement functionElement = createDummyBashFile(project, name + "() { x; }");
        return functionElement.getFirstChild().getFirstChild();
    }

    public static PsiElement createWord(Project project, String name) {
        return createDummyBashFile(project, name).getFirstChild();
    }

    public static PsiElement createAssignmentWord(Project project, String name) {
        final PsiElement assignmentCommand = createDummyBashFile(project, name + "=a").getFirstChild();

        return assignmentCommand.getFirstChild().getFirstChild();
    }

    public static PsiElement createVariable(Project project, String name, boolean withBraces) {
        try {
            if (withBraces) {
                String text = "${" + name + "}";
                PsiElement command = createDummyBashFile(project, text).getFirstChild();

                //fixme terrible code
                return command.getFirstChild().getFirstChild().getFirstChild().getNextSibling().getFirstChild().getNextSibling();
            }

            String text = "$" + name;
            PsiElement command = createDummyBashFile(project, text).getFirstChild();

            return command.getFirstChild().getFirstChild();
        } catch (Exception e) {
            throw new RuntimeException("Exception while trying to create replacement variable for '" + name + "' ", e);
        }
    }

    public static PsiElement createShebang(Project project, String command, boolean addNewline) {
        String text = "#!" + command + (addNewline ? "\n" : "");
        return createDummyBashFile(project, text).getFirstChild();
    }
}
