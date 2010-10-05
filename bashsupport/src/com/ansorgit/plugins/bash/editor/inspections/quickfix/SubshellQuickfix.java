/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: SubshellQuickfix.java, Class: SubshellQuickfix
 * Last modified: 2010-03-24
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

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.lang.psi.api.expression.BashSubshellCommand;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Replaces a subshell command with the old-style backtick command.
 * <p/>
 * User: jansorg
 * Date: 21.05.2009
 * Time: 14:05:02
 */
public class SubshellQuickfix extends AbstractBashQuickfix {
    private final BashSubshellCommand subshellCommand;

    public SubshellQuickfix(BashSubshellCommand subshellCommand) {
        this.subshellCommand = subshellCommand;
    }

    @NotNull
    public String getName() {
        return "Replace with backquote command";
    }

    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document != null) {
            int startOffset = subshellCommand.getTextOffset() - 1; //to include the $
            int endOffset = subshellCommand.getTextRange().getEndOffset();
            String command = subshellCommand.getCommandText();

            document.replaceString(startOffset, endOffset, "`" + command + "`");
            PsiDocumentManager.getInstance(project).commitDocument(document);
        }
    }
}
