/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFileType.java, Class: BashFileType
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

package com.ansorgit.plugins.bash.file;

import com.ansorgit.plugins.bash.lang.Bash;
import com.ansorgit.plugins.bash.lang.BashLanguage;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.ansorgit.plugins.bash.util.content.BashContentUtil;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * The file type implementation for Bash files.
 *
 * @author Joachim Ansorg
 */
public class BashFileType extends LanguageFileType implements FileTypeIdentifiableByVirtualFile {
    public static final BashFileType BASH_FILE_TYPE = new BashFileType();
    public static final Language BASH_LANGUAGE = BASH_FILE_TYPE.getLanguage();

    /**
     * The default file extension of bash scripts.
     */
    public static final String DEFAULT_EXTENSION = "sh";
    public static final String BASH_EXTENSION = "bash";

    /**
     * All extensions which are associated with this plugin.
     */
    public static final String[] extensions = {DEFAULT_EXTENSION, BASH_EXTENSION};
    public static final List<String> extensionList = Arrays.asList(extensions);

    //needed for the automatic file content type guessing
    private static final double MIN_FILE_PROBABILIY = 0.75d;

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
     *
     * @param file The file to check
     * @return True if BashSupport wants to take that file
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isMyFileType(@NotNull VirtualFile file) {
        if (extensionList.contains(file.getExtension())) {
            return true;
        }

        return StringUtils.isEmpty(file.getExtension()) && BashContentUtil.isProbablyBashFile(VfsUtil.virtualToIoFile(file), MIN_FILE_PROBABILIY);
    }

}
