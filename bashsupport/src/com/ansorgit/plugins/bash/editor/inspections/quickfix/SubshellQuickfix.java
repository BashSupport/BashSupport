/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: SubshellQuickfix.java, Class: SubshellQuickfix
 * Last modified: 2011-04-30 16:33
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

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.editor.inspections.InspectionProvider;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashSubshellCommand;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Replaces a subshell command with the old-style backtick command.
 */
public class SubshellQuickfix extends LocalQuickFixAndIntentionActionOnPsiElement {
    public SubshellQuickfix() {
        super(null);
    }

    @NotNull
    public String getText() {
        return "Replace with backquote command";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return InspectionProvider.FAMILY;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        return startElement instanceof BashSubshellCommand;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document != null) {
            BashSubshellCommand subshellCommand = (BashSubshellCommand) startElement;

            //fixme check if the element is inside of a $() block

            int startOffset = subshellCommand.getTextOffset(); //to include the $
            int endOffset = subshellCommand.getTextRange().getEndOffset();
            String command = subshellCommand.getCommandText();

            document.replaceString(startOffset, endOffset, "`" + command + "`");

            PsiDocumentManager.getInstance(project).commitDocument(document);
        }
    }
}
