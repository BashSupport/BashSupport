/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ModuleFileFilter.java, Class: ModuleFileFilter
 * Last modified: 2010-02-13
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

/*
 */

package com.ansorgit.plugins.bash.settings.facet.ui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;

/**
 * User: jansorg
 * Date: Feb 12, 2010
 * Time: 10:39:47 PM
 */
class ModuleFileFilter implements VirtualFileFilter {
    private final Module module;

    public ModuleFileFilter(Module module) {
        this.module = module;
    }

    public boolean accept(VirtualFile virtualFile) {
        return !(!virtualFile.isDirectory() && virtualFile.getExtension() != null);
    }
}
