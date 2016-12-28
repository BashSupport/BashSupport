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

package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author jansorg
 */
public final class BashSearchScopes {
    private BashSearchScopes() {
    }

    public static GlobalSearchScope moduleScope(PsiFile file) {
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) {
            return GlobalSearchScope.EMPTY_SCOPE;
        }

        Module module = ProjectRootManager.getInstance(file.getProject()).getFileIndex().getModuleForFile(virtualFile);
        if (module == null) {
            return GlobalSearchScope.fileScope(file);
        }

        //the module scope returned by getModuleScope() just contains the files in the configured source and test source directories,
        //module content scope includes all files in the module directory
        return module.getModuleContentScope();
    }

    public static GlobalSearchScope bashOnly(GlobalSearchScope scope) {
        return GlobalSearchScope.getScopeRestrictedByFileTypes(scope, BashFileType.BASH_FILE_TYPE);
    }
}
