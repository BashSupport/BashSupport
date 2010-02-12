/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: FileTreeTable.java, Class: FileTreeTable
 * Last modified: 2010-02-12
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

package com.ansorgit.plugins.bash.settings.facet.ui;

import com.intellij.openapi.module.Module;

/**
 * User: jansorg
 * Date: Feb 12, 2010
 * Time: 10:35:52 PM
 */
public class FileTreeTable extends com.intellij.util.ui.tree.AbstractFileTreeTable<FileMode> {
    public FileTreeTable(Module module) {
        super(module.getProject(), FileMode.class, "Ignore", new ModuleFileFilter(module));
    }

}
