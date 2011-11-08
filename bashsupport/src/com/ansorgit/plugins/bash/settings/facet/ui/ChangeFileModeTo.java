/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ChangeFileModeTo.java, Class: ChangeFileModeTo
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
package com.ansorgit.plugins.bash.settings.facet.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author cdr
 */
class ChangeFileModeTo extends AnAction implements DumbAware {
    private final VirtualFile myFile;
    private final FileMode myMode;

    ChangeFileModeTo(@Nullable VirtualFile file, @NotNull FileMode mode) {
        super(mode.getDisplayName(), "Change " + (file == null ? "default" : "file '" + file.getName() + "'") +
                " mode to '" + mode.getDisplayName() + "'.", null);
        myFile = file;
        myMode = mode;
    }

    public void actionPerformed(final AnActionEvent e) {
        chosen(myFile, myMode);
    }

    protected void chosen(final VirtualFile file, final FileMode mode) {
        //EncodingManager.getInstance().setEncoding(file, charset);
    }
}
