/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashChangeUtil.java, Class: BashChangeUtil
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

package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;

/**
 * Date: 16.04.2009
 * Time: 16:48:49
 *
 * @author Joachim Ansorg
 */
public class BashChangeUtil {
    public static void replaceText(PsiFile file, TextRange range, String replacement) {
        Document document = file.getViewProvider().getDocument();
        assert document != null;

        document.replaceString(range.getStartOffset(), range.getEndOffset(), replacement);
    }

    public static PsiElement createTreeFromText(Project project, String text) {
        ParserDefinition def = LanguageParserDefinitions.INSTANCE.forLanguage(BashFileType.BASH_LANGUAGE);
        assert def != null;

        String filename = "dummy.sh";
        final PsiFile dummyFile = PsiFileFactory.getInstance(project).createFileFromText(filename, text);
        return dummyFile.getFirstChild();
    }

    public static PsiElement createSymbol(Project project, String name) {
        final PsiElement functionElement = createTreeFromText(project, name + "() { x; }");
        return functionElement.getFirstChild();
    }

    public static PsiElement createWord(Project project, String name) {
        return createTreeFromText(project, name);
    }

    public static PsiElement createAssignmentWord(Project project, String name) {
        final PsiElement assignmentCommand = createTreeFromText(project, name + "=a");
        PsiElement varDef = assignmentCommand.getFirstChild();
        return varDef != null ? varDef.getFirstChild() : null;
    }

    public static PsiElement createVariable(Project project, String name, boolean withBraces) {
        final String text = withBraces ? "${" + name + "}" : "$" + name;
        return createTreeFromText(project, text).getFirstChild();
    }

    public static PsiElement createShebang(Project project, String command, boolean addNewline) {
        final PsiElement psi = createTreeFromText(project, "#!" + command + (addNewline ? "\n" : ""));
        return psi.getFirstChild();
    }

}
