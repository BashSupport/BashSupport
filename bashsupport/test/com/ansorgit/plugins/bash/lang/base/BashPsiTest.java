/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPsiTest.java, Class: BashPsiTest
 * Last modified: 2010-02-01
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

package com.ansorgit.plugins.bash.lang.base;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

/**
 * User: jansorg
 * Date: Jan 11, 2010
 * Time: 10:30:39 PM
 */
public abstract class BashPsiTest extends LightCodeInsightFixtureTestCase {
    protected VirtualFile getFile(final String relative_path, final Module module) {
        final String path = getFullPath(relative_path, module);
        return LocalFileSystem.getInstance().findFileByPath(path);
    }

    protected String getFullPath(String relative_path, Module module) {
        final VirtualFile[] sourceRootUrls = ModuleRootManager.getInstance(module).getContentRoots();
        return getBasePath() + "/" + relative_path;
    }

    @Override
    protected String getBasePath() {
        return "/home/jansorg/Projekte/JavaProjekte/BashSupport/testdata/psi";
    }

}
