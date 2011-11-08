/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ModuleFileFilter.java, Class: ModuleFileFilter
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

/*
 */

package com.ansorgit.plugins.bash.settings.facet.ui;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;

/**
 * User: jansorg
 * Date: Feb 12, 2010
 * Time: 10:39:47 PM
 */
class ModuleFileFilter implements VirtualFileFilter {
    public ModuleFileFilter() {
    }

    public boolean accept(VirtualFile virtualFile) {
        if (virtualFile.isDirectory()) {
            boolean hasValidSubtree = false;

            for (VirtualFile f : virtualFile.getChildren()) {
                if (accept(f)) {
                    hasValidSubtree = true;
                    break;
                }
            }

            return hasValidSubtree;
        }

        return virtualFile.getExtension() == null;
    }
}
