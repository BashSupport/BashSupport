/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFileType.java, Class: BashFileType
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

import com.ansorgit.plugins.bash.lang.Bash;
import com.ansorgit.plugins.bash.lang.BashLanguage;
import com.ansorgit.plugins.bash.settings.facet.BashFacet;
import com.ansorgit.plugins.bash.settings.facet.BashFacetConfiguration;
import com.ansorgit.plugins.bash.settings.facet.ui.FileMode;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 22.03.2009
 * Time: 11:08:04
 *
 * @author Joachim Ansorg
 */
public class BashFileType extends LanguageFileType implements FileTypeIdentifiableByVirtualFile {
    private static final Logger LOG = Logger.getInstance("#BashFileType");
    public static final BashFileType BASH_FILE_TYPE = new BashFileType();
    public static final Language BASH_LANGUAGE = BASH_FILE_TYPE.getLanguage();
    /**
     * The default file extension of bash scripts.
     */
    public static final String DEFAULT_EXTENSION = "sh";

    /**
     * All extensions which are associated with this plugin.
     */
    public static final String[] extensions = {DEFAULT_EXTENSION, "bash"};
    public static final List<String> extensionList = Arrays.asList(extensions);

    public static final List<String> validContentStarts = Arrays.asList("#!/bin/sh", "#!/bin/bash", "#!/usr/bin/sh", "#!/usr/bin/bash");

    protected BashFileType() {
        super(new BashLanguage());
    }

    @NotNull
    public String getName() {
        return Bash.lanuageName;
    }

    @NotNull
    public String getDescription() {
        return Bash.languageDescription;
    }

    @NotNull
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    public Icon getIcon() {
        return BashIcons.BASH_FILE_ICON;
    }

    /**
     * Here we check if a given file belongs to our plugin.
     * We take this road because we need the actual file and not a filename to check files without extension.
     *
     * @param file The file to check
     * @return True if BashSupport wants to take that file
     */
    public boolean isMyFileType(VirtualFile file) {
        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {
            return false;
        }

        if (extensionList.contains(file.getExtension())) {
            return true;
        } else if (!file.isInLocalFileSystem()) {
            return false;
        } else if (StringUtils.isEmpty(file.getExtension())) {
            //no extensions, special checks (looking at the content, etc)

            //guess project
            Project project = ProjectUtil.guessProjectForFile(file);
            if (project == null) {
                return false;
            }

            Module module = ModuleUtil.findModuleForFile(file, project);
            if (module == null) {
                return false;
            }

            BashFacet facet = BashFacet.getInstance(module);
            if (facet == null) {
                return false;
            }

            BashFacetConfiguration config = facet.getConfiguration();
            FileMode mode = config.findMode(file);

            if (mode == FileMode.accept()) {
                return true;
            } else if (mode == FileMode.ignore()) {
                return false;
            } else if (mode == FileMode.auto()) {
                if (file.getLength() == 0) {
                    return true;
                }

                try {
                    byte[] data = new byte[48];
                    InputStream inputStream = file.getInputStream();

                    //the guess logic should be improved
                    int read = inputStream.read(data, 0, 10);
                    if (read > 0) {
                        String content = new String(data);
                        for (String s : validContentStarts) {
                            if (content.startsWith(s)) {
                                return true;
                            }
                        }
                    }
                } catch (IOException e) {
                    LOG.warn("Error checking file content for Bash", e);
                }
            }
        }

        return false;
    }
}
