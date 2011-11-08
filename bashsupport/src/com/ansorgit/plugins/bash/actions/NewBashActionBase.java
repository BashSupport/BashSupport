/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: NewBashActionBase.java, Class: NewBashActionBase
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

package com.ansorgit.plugins.bash.actions;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.CommonBundle;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.ansorgit.plugins.bash.file.BashFileType.DEFAULT_EXTENSION;

/**
 * Date: 17.04.2009
 * Time: 20:20:20
 *
 * @author Joachim Ansorg
 */
abstract class NewBashActionBase extends CreateElementActionBase {
    private static final Logger log = Logger.getInstance("#NewActionBase");

    public NewBashActionBase(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    @NotNull
    protected final PsiElement[] invokeDialog(final Project project, final PsiDirectory directory) {
        log.debug("invokeDialog");
        final MyInputValidator validator = new MyInputValidator(project, directory);
        Messages.showInputDialog(project, getDialogPrompt(), getDialogTitle(), Messages.getQuestionIcon(), "", validator);

        final PsiElement[] elements = validator.getCreatedElements();
        log.debug("Result: " + elements);
        return elements;
    }

    public void update(final AnActionEvent event) {
        log.debug("update");
        super.update(event);

        final Presentation presentation = event.getPresentation();
        final DataContext context = event.getDataContext();
        Module module = (Module) context.getData(LangDataKeys.MODULE.getName());

        log.debug("update: module: " + module);

        final boolean hasModule = module != null;
        presentation.setEnabled(hasModule);
        presentation.setVisible(hasModule);
    }

    protected static PsiFile createFileFromTemplate(final PsiDirectory directory,
                                                    String className,
                                                    @NonNls String templateName,
                                                    @NonNls String... parameters) throws IncorrectOperationException {
        log.debug("createFileFromTemplate");

        String usedExtension = FileUtil.getExtension(className);
        boolean withExtension = BashFileType.extensionList.contains(usedExtension.toLowerCase());

        String filename = withExtension ? className : className + "." + DEFAULT_EXTENSION;
        return BashTemplatesFactory.createFromTemplate(directory, className, filename);
    }

    @NotNull
    protected PsiElement[] create(String newName, PsiDirectory directory) throws Exception {
        log.debug("create " + newName + ", dir: " + directory);
        return doCreate(newName, directory);
    }

    @NotNull
    protected abstract PsiElement[] doCreate(String newName, PsiDirectory directory);

    protected abstract String getDialogPrompt();

    protected abstract String getDialogTitle();

    protected String getErrorTitle() {
        return CommonBundle.getErrorTitle();
    }

    protected void checkBeforeCreate(String newName, PsiDirectory directory) throws IncorrectOperationException {
        checkCreateFile(directory, newName);
    }

    public static void checkCreateFile(@NotNull PsiDirectory directory, String name) throws IncorrectOperationException {
        final String fileName = name + "." + DEFAULT_EXTENSION;
        directory.checkCreateFile(fileName);
    }
}
