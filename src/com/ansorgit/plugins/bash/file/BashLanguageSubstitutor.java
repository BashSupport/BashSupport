/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashLanguageSubstitutor.java, Class: BashLanguageSubstitutor
 * Last modified: 2010-02-17
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

package com.ansorgit.plugins.bash.file;

import com.ansorgit.plugins.bash.settings.facet.BashFacet;
import com.ansorgit.plugins.bash.settings.facet.BashFacetConfiguration;
import com.ansorgit.plugins.bash.settings.facet.ui.FileMode;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.LanguageSubstitutor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: jansorg
 * Date: Feb 17, 2010
 * Time: 6:29:18 PM
 */
public class BashLanguageSubstitutor extends LanguageSubstitutor {
    private static final Logger LOG = Logger.getInstance("#BashLanguageSubstitutor");

    @Override
    public Language getLanguage(@NotNull VirtualFile file, @NotNull Project project) {
        if (file.isDirectory()) {
            return null;
        }

        if (BashFileType.extensionList.contains(file.getExtension())) {
            return BashFileType.BASH_LANGUAGE;
        }

        boolean isBashFile = false;

        if (file.isInLocalFileSystem() && StringUtil.isEmpty(file.getExtension())) {
            Module module = ModuleUtil.findModuleForFile(file, project);
            BashFacet facet = BashFacet.getInstance(module);

            if (facet != null) {
                BashFacetConfiguration configuration = facet.getConfiguration();
                BashFacetConfiguration.OperationMode mode = configuration.getOperationMode();
                if (mode == BashFacetConfiguration.OperationMode.AcceptAll) {
                    isBashFile = true;
                } else if (mode == BashFacetConfiguration.OperationMode.Custom) {
                    FileMode fileMode = configuration.findMode(file);
                    if (fileMode == FileMode.auto()) {
                        try {
                            byte[] data = new byte[48];
                            InputStream inputStream = file.getInputStream();

                            //the guess logic should be improved
                            int read = inputStream.read(data, 0, 10);
                            if (read > 0) {
                                String content = new String(data);
                                for (String s : BashFileType.validContentStarts) {
                                    if (content.startsWith(s)) {
                                        isBashFile = true;
                                        break;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            LOG.warn("Error checking file content for Bash", e);
                        }
                    } else {
                        isBashFile = fileMode == FileMode.accept();
                    }
                }
            }
        }

        return isBashFile ? BashFileType.BASH_LANGUAGE : Language.ANY;
    }
}
