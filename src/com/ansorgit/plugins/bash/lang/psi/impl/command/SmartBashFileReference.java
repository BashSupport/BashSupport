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

package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashSearchScopes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Reference to another file, used in include commands. This implementation handles the dumb mode and turns off index
 * access during dumb  mode operations.
 *
 * @author jansorg
 */
class SmartBashFileReference extends AbstractBashFileReference {

    public SmartBashFileReference(AbstractBashCommand<?> cmd) {
        super(cmd);
    }

    @Nullable
    @Override
    public PsiElement resolveInner() {
        final String referencedName = cmd.getReferencedCommandName();
        if (referencedName == null) {
            return null;
        }

        String fileName = PathUtil.getFileName(referencedName);
        GlobalSearchScope scope = BashSearchScopes.moduleScope(cmd.getContainingFile());

        PsiFileSystemItem[] files = FilenameIndex.getFilesByName(cmd.getProject(), fileName, scope, false);
        if (files.length == 0) {
            return null;
        }

        PsiFile currentFile = cmd.getContainingFile();
        return BashPsiFileUtils.findRelativeFile(currentFile, referencedName);
    }

}
