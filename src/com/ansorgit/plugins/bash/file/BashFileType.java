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

package com.ansorgit.plugins.bash.file;

import com.ansorgit.plugins.bash.lang.BashLanguage;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * The file type implementation for Bash files.
 *
 * @author jansorg
 */
public class BashFileType extends LanguageFileType {
    public static final BashFileType BASH_FILE_TYPE = new BashFileType();
    public static final Language BASH_LANGUAGE = BASH_FILE_TYPE.getLanguage();

    /**
     * The default file extension of bash scripts.
     */
    public static final String SH_EXTENSION = "sh";
    static final String BASH_EXTENSION = "bash";
    
    static final String BASHRC_FILENAME = ".bashrc";
    static final String PROFILE_FILENAME = ".profile";
    static final String BASH_PROFILE_FILENAME = ".bash_profile";
    static final String BASH_LOGOUT_FILENAME = ".bash_logout";
    static final String BASH_ALIASES_FILENAME = ".bash_aliases";

    public static final String[] BASH_SPECIAL_FILES = new String[]{
            BASHRC_FILENAME, PROFILE_FILENAME, BASH_PROFILE_FILENAME, BASH_LOGOUT_FILENAME, BASH_ALIASES_FILENAME};

    protected BashFileType() {
        super(new BashLanguage());
    }

    @Override
    public String toString() {
        return "BashFileType";
    }

    @NotNull
    public String getName() {
        return "Bash";
    }

    @NotNull
    public String getDescription() {
        return "Bourne Again Shell";
    }

    @NotNull
    public String getDefaultExtension() {
        return SH_EXTENSION;
    }

    public Icon getIcon() {
        return BashIcons.BASH_FILE_ICON;
    }
}
