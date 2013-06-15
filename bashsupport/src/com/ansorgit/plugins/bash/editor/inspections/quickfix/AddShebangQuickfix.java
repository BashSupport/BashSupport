/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: AddShebangQuickfix.java, Class: AddShebangQuickfix
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

import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 15.05.2009
 * Time: 16:15:38
 *
 * @author Joachim Ansorg
 */
public class AddShebangQuickfix extends AbstractBashQuickfix {

    @NotNull
    public String getName() {
        return "Add shebang line";
    }

    public void invoke(@NotNull final Project project, Editor editor, final PsiFile file) throws IncorrectOperationException {
        // work around a problem in a 9.0.2 eap which need a write session
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                Document document = PsiDocumentManager.getInstance(project).getDocument(BashPsiUtils.findFileContext(file));
                if (document != null) {
                    document.insertString(0, "#!/bin/sh\n");
                    PsiDocumentManager.getInstance(project).commitDocument(document);
                }
            }
        });
    }

}
