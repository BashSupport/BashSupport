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

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.lang.psi.api.BashBackquote;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Quickfix to convert a backtick command into a subshell command.
 * <br>
 * @author jansorg
 */
public class BackquoteQuickfix extends AbstractBashPsiElementQuickfix {
    public BackquoteQuickfix(BashBackquote backquote) {
        super(backquote);
    }

    @Override
    @NotNull
    public String getText() {
        return "Replace with subshell command";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document != null) {
            BashBackquote backquote = (BashBackquote) startElement;
            int endOffset = startElement.getTextRange().getEndOffset();
            String command = backquote.getCommandText();

            //fixme replace this with a PSI element created by the Bash psi factory
            document.replaceString(startElement.getTextOffset(), endOffset, "$(" + command + ")");

            PsiDocumentManager.getInstance(project).commitDocument(document);
        }
    }
}
