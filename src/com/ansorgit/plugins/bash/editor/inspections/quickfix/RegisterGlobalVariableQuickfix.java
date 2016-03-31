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

/*
 */

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ReadOnlyFragmentModificationException;
import com.intellij.openapi.editor.ReadOnlyModificationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Quickfix to register an unknown / unresolved variable as a globally defined variable.
 * <br>
 * @author jansorg
 */
public class RegisterGlobalVariableQuickfix extends AbstractBashPsiElementQuickfix {
    public RegisterGlobalVariableQuickfix(BashVar bashVar) {
        super(bashVar);
    }

    @NotNull
    public String getText() {
        return "Register as global variable";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull final PsiFile file, Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        BashVar bashVar = (BashVar) startElement;

        String variableName = bashVar.getReference().getReferencedName();
        TextRange textRange = bashVar.getTextRange();
        BashProjectSettings.storedSettings(project).addGlobalVariable(variableName);

        //replace this position with the same value, we have to trigger a reparse somehow
        try {
            file.getViewProvider().getDocument().replaceString(textRange.getStartOffset(), textRange.getEndOffset(), bashVar.getText());
        } catch (ReadOnlyModificationException e) {
            //ignore
        } catch (ReadOnlyFragmentModificationException e) {
            //ignore
        }

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                ApplicationManager.getApplication().saveSettings();
                file.subtreeChanged(); //FIXME RIGHT?
            }
        });
    }
}
