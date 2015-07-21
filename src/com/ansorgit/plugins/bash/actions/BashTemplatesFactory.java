/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashTemplatesFactory.java, Class: BashTemplatesFactory
 * Last modified: 2013-01-31
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

package com.ansorgit.plugins.bash.actions;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.ansorgit.plugins.bash.util.BashStrings;
import com.intellij.ide.fileTemplates.*;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;

import java.util.Properties;

/**
 * This is a factory for new Bash files.
 *
 * @author Joachim Ansorg
 */
public class BashTemplatesFactory implements FileTemplateGroupDescriptorFactory {
    public static final String DEFAULT_TEMPLATE_FILENAME = "Bash Script.sh";

    public BashTemplatesFactory() {
    }

    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        FileTemplateGroupDescriptor templateGroup = new FileTemplateGroupDescriptor(BashStrings.message("file.template.group.title.bash"), BashIcons.BASH_FILE_ICON);
        templateGroup.addTemplate(new FileTemplateDescriptor(DEFAULT_TEMPLATE_FILENAME, FileTypeManager.getInstance().getFileTypeByFileName(DEFAULT_TEMPLATE_FILENAME).getIcon()));

        return templateGroup;
    }

    public static PsiFile createFromTemplate(final PsiDirectory directory, String fileName, String templateName) throws IncorrectOperationException {
        Project project = directory.getProject();
        FileTemplate template = FileTemplateManager.getInstance().getInternalTemplate(templateName);

        Properties properties = new Properties(FileTemplateManager.getInstance().getDefaultProperties(project));

        String templateText;
        try {
            templateText = template.getText(properties);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load template for " + FileTemplateManager.getInstance().internalTemplateToSubject(templateName), e);
        }

        final PsiFileFactory factory = PsiFileFactory.getInstance(project);

        PsiFile file = factory.createFileFromText(fileName, BashFileType.BASH_FILE_TYPE, templateText);
        file = (PsiFile) directory.add(file);

        /*
        if (file != null && allowReformatting && template.isReformatCode()) {
            new ReformatCodeProcessor(project, file, null, false).run();
        }
        */

        return file;
    }

}
