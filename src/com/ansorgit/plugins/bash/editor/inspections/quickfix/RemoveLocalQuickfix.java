/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: RemoveLocalQuickfix.java, Class: RemoveLocalQuickfix
 * Last modified: 2010-12-28 14:57
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

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
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
public class RemoveLocalQuickfix extends AbstractBashQuickfix {
    private final BashVarDef varDef;

    public RemoveLocalQuickfix(BashVarDef varDef) {
        this.varDef = varDef;
    }

    @NotNull
    public String getName() {
        return "Remove local part of the definition";
    }

    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement context = varDef.getContext();
        if (context != null) {
            //a definition without value, e.g. "local a" has to be relaced with a= , otherwise it's not a valid
            //var def
            if (!varDef.hasAssignmentValue()) {
                Document document = PsiDocumentManager.getInstance(project).getDocument(varDef.getContainingFile());
                if (document != null) {
                    int endOffset = context.getTextOffset() + context.getTextLength();
                    document.replaceString(context.getTextOffset(), endOffset, varDef.getName() + "=");

                    PsiDocumentManager.getInstance(project).commitDocument(document);
                }
            } else {
                BashPsiUtils.replaceElement(context, varDef);
            }
        }
    }
}
