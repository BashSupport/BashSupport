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

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Properties;

/**
 * Factory for new bash files. It is used by the {@link NewBashFileAction} action.
 *
 * @author jansorg
 */
class BashTemplatesFactory {
    static final String DEFAULT_TEMPLATE_FILENAME = "Bash Script.sh";

    @NotNull
    static PsiFile createFromTemplate(final PsiDirectory directory, String fileName, String templateName) throws IncorrectOperationException {
        Project project = directory.getProject();
        FileTemplateManager templateManager = FileTemplateManager.getInstance(project);
        FileTemplate template = templateManager.getInternalTemplate(templateName);

        Properties properties = new Properties(templateManager.getDefaultProperties());

        String templateText;
        try {
            templateText = template.getText(properties);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load template for " + templateManager.internalTemplateToSubject(templateName), e);
        }

        PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(fileName, BashFileType.BASH_FILE_TYPE, templateText);
        return (PsiFile) directory.add(file);
    }
}