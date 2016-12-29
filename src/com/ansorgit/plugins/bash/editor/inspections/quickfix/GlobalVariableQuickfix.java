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
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.command.undo.GlobalUndoableAction;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.command.undo.UnexpectedUndoException;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.FileContentUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * Quickfix to register an unknown / unresolved variable as a globally defined variable.
 * <br>
 *
 * @author jansorg
 */
public class GlobalVariableQuickfix extends AbstractBashPsiElementQuickfix {
    private final boolean register;

    public GlobalVariableQuickfix(BashVar bashVar, boolean register) {
        super(bashVar);
        this.register = register;
    }

    @NotNull
    public String getText() {
        return register ? "Register as global variable" : "Unregister as global variable";
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull final PsiFile file, Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        BashVar variable = (BashVar) startElement;
        String variableName = variable.getReference().getReferencedName();

        UndoConfirmationPolicy mode = ApplicationManager.getApplication().isUnitTestMode()
                ? UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION
                : UndoConfirmationPolicy.DEFAULT;

        CommandProcessor.getInstance().executeCommand(project, new GlobalVarRegistryAction(project, variableName, register), getText(), null, mode);
    }

    /**
     * This class handles the registry action and adds an undo/redo step.
     */
    private static class GlobalVarRegistryAction implements Runnable {
        private final Project project;
        private final String variableName;
        private final boolean register;

        public GlobalVarRegistryAction(Project project, String variableName, boolean register) {
            this.project = project;
            this.variableName = variableName;
            this.register = register;
        }

        private void doRegistryAction(boolean register) {
            if (register) {
                BashProjectSettings.storedSettings(project).addGlobalVariable(variableName);
            } else {
                BashProjectSettings.storedSettings(project).removeGlobalVariable(variableName);
            }

            FileContentUtil.reparseFiles(project, Collections.emptyList(), true);
        }

        public void run() {
            VirtualFile[] openFiles = FileEditorManager.getInstance(project).getOpenFiles();

            UndoManager.getInstance(project).undoableActionPerformed(new GlobalUndoableAction(openFiles) {
                @Override
                public void undo() throws UnexpectedUndoException {
                    doRegistryAction(!register);
                }

                @Override
                public void redo() throws UnexpectedUndoException {
                    doRegistryAction(register);
                }
            });

            doRegistryAction(register);
        }
    }
}
