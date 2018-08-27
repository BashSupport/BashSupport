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

import com.ansorgit.plugins.bash.util.BashInterpreterDetection;
import com.google.common.collect.Lists;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.util.io.ByteSequence;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * FileTypeDetector implementation which can detect a Bash file by content. Only files without an extensions are checked.
 * Files with extensions should be associated with BashSupport using the available configuration options of IntelliJ.
 *
 * @author jansorg
 */
public class BashFileTypeDetector implements FileTypeRegistry.FileTypeDetector {
    private static final List<String> VALID_SHEBANGS = Lists.newArrayList();

    static {
        for (String location : BashInterpreterDetection.POSSIBLE_LOCATIONS) {
            VALID_SHEBANGS.add("#!" + location);
        }
    }

    @Nullable
    @Override
    public FileType detect(@NotNull VirtualFile file, @NotNull ByteSequence firstBytes, @Nullable CharSequence textContent) {
        return detect(file, textContent);
    }

    @Nullable
    public static FileType detect(@NotNull VirtualFile file, @Nullable CharSequence textContent) {
        if (textContent == null || file.getExtension() != null) {
            return null;
        }

        String content = textContent.toString();

        for (String shebang : VALID_SHEBANGS) {
            // more efficient would be to  check only at the beginning of the sample content,
            // but this would need smart whitespace stripping for now, this is good enough
            if (content.contains(shebang)) {
                return BashFileType.BASH_FILE_TYPE;
            }
        }

        return null;
    }

    @Override
    public int getVersion() {
        return 1;
    }
}
