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

package com.ansorgit.plugins.bash.actions;

import com.ansorgit.plugins.bash.util.BashIcons;
import com.ansorgit.plugins.bash.util.BashStrings;
import com.intellij.CommonBundle;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.ansorgit.plugins.bash.file.BashFileType.SH_EXTENSION;

/**
 * Action to create a new Bash file from a template.
 * <br>
 * The template data is stored in resources/fileTemplates/internal/Bash Script.sh.ft
 *
 * @author jansorg
 */
public class NewBashFileAction extends CreateElementActionBase {
    public NewBashFileAction() {
        super(BashStrings.message("newfile.menu.action.text"), BashStrings.message("newfile.menu.action.description"), BashIcons.BASH_FILE_ICON);
    }

    static String computeFilename(String inputFilename) {
        String usedExtension = FileUtilRt.getExtension(inputFilename);
        boolean withExtension = !usedExtension.isEmpty();

        return withExtension ? inputFilename : (inputFilename + "." + SH_EXTENSION);
    }

    private String getDialogPrompt() {
        return BashStrings.message("newfile.dialog.prompt");
    }

    private String getDialogTitle() {
        return BashStrings.message("newfile.dialog.title");
    }

    protected String getCommandName() {
        return BashStrings.message("newfile.command.name");
    }

    protected String getActionName(PsiDirectory directory, String newName) {
        return BashStrings.message("newfile.menu.action.text");
    }

    @NotNull
    protected final PsiElement[] invokeDialog(final Project project, final PsiDirectory directory) {
        final MyInputValidator validator = new MyInputValidator(project, directory);
        Messages.showInputDialog(project, getDialogPrompt(), getDialogTitle(), Messages.getQuestionIcon(), "", validator);

        return validator.getCreatedElements();
    }

    @NotNull
    protected PsiElement[] create(String newName, PsiDirectory directory) throws Exception {
        PsiFile file = BashTemplatesFactory.createFromTemplate(directory, computeFilename(newName), BashTemplatesFactory.DEFAULT_TEMPLATE_FILENAME);

        File ioFile = VfsUtil.virtualToIoFile(file.getVirtualFile());
        if (ioFile.exists()) {
            ioFile.setExecutable(true, true);
        }

        PsiElement child = file.getLastChild();
        return child != null ? new PsiElement[]{file, child} : new PsiElement[]{file};
    }

    protected String getErrorTitle() {
        return CommonBundle.getErrorTitle();
    }
}
