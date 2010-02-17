/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFileTypeLoader.java, Class: BashFileTypeLoader
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

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class BashFileTypeLoader extends FileTypeFactory {
    //private Logger log = Logger.getInstance("#bash.FileTypeLoader");

    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(BashFileType.BASH_FILE_TYPE, BashFileType.DEFAULT_EXTENSION);
        consumer.consume(BashFileType.BASH_FILE_TYPE, "bash");

        /*consumer.consume(BashFileType.BASH_FILE_TYPE, new FileNameMatcher() {
            public boolean accept(@NonNls String fileName) {
                return StringUtils.isNotEmpty(fileName) && FileUtil.getExtension(fileName).length() == 0;
            }

            @NotNull
            public String getPresentableString() {
                return "Bash";  //To change body of implemented methods use File | Settings | File Templates.
            }
        });*/
    }

    /* private static final class FileNameMatcherDelegate implements FileNameMatcher {
        private final FileNameMatcher delegate;
        private boolean checked = false;
        private boolean enabled = false;

        private FileNameMatcherDelegate(FileNameMatcher delegate) {
            this.delegate = delegate;
        }

        private boolean checkEnabled() {
            final BashLoader bashLoader = BashLoader.getInstance();
            if (bashLoader == null || bashLoader.getSettingsComponent() == null) return false;

            return bashLoader.getSettingsComponent().getState().isLoadEmptyExtensions();
        }

        public boolean accept(@NonNls String filename) {
            if (!checked) {
                checked = true;
                enabled = checkEnabled();
            }

            return checked && enabled && delegate.accept(filename);
        }

        @NotNull
        public String getPresentableString() {
            return delegate.getPresentableString();
        }
    }

    private static class BashFileNameMatcher implements FileNameMatcher {
        public boolean accept(@NonNls String filename) {
            final String extension = FileUtil.getExtension(filename);
            return extension.length() == 0; //don't use isEmpty(), only exists in Java 6
        }

        @NotNull
        public String getPresentableString() {
            return "Bash";
        }
    }*/
}
