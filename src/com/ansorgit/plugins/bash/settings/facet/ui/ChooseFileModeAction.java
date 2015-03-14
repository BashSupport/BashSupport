/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ChooseFileModeAction.java, Class: ChooseFileModeAction
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
 * Created by IntelliJ IDEA.
 * User: cdr
 * Date: Jul 19, 2007
 * Time: 5:53:46 PM
 */
package com.ansorgit.plugins.bash.settings.facet.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

abstract class ChooseFileModeAction extends ComboBoxAction {
    private final VirtualFile myVirtualFile;

    public ChooseFileModeAction(VirtualFile virtualFile) {
        myVirtualFile = virtualFile;
    }

    public void update(final AnActionEvent e) {
        boolean enabled = isEnabled(myVirtualFile);

        if (myVirtualFile != null) {
            FileMode mode = modeFromFile(myVirtualFile);
            if (mode == null) {
                mode = FileMode.auto();
            }

            e.getPresentation().setText(mode.getDisplayName());
        }

        e.getPresentation().setEnabled(enabled);
    }

    public static boolean isEnabled(VirtualFile virtualFile) {
        boolean enabled = true;
        if (virtualFile != null) {
            //FileMode mode = modeFromFile(virtualFile);
            /*if (mode != null) {
                enabled = false;
            } else*/
            if (!virtualFile.isDirectory()) {
                FileType fileType = FileTypeManager.getInstance().getFileTypeByFile(virtualFile);
                if (StringUtil.isNotEmpty(virtualFile.getExtension()) && fileType.isBinary()) {
                    enabled = false;
                }
            }
        }

        return enabled;
    }

    @Nullable("returns null if filemode cannot be determined from content")
    public static FileMode modeFromFile(final VirtualFile virtualFile) {
        if (virtualFile == null) {
            return null;
        }

        final Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        if (document == null) {
            return null;
        }

        return FileMode.defaultMode();
    }

    @NotNull
    protected DefaultActionGroup createPopupActionGroup(final JComponent button) {
        return createGroup(true);
    }

    private void fileModeActions(DefaultActionGroup group, final VirtualFile virtualFile, List<FileMode> fileModes) {
        for (FileMode slave : fileModes) {
            ChangeFileModeTo action = new ChangeFileModeTo(virtualFile, slave) {
                protected void chosen(final VirtualFile file, final FileMode mode) {
                    ChooseFileModeAction.this.chosen(file, mode);
                }
            };

            group.add(action);
        }
    }

    private class ClearThisFileModeAction extends AnAction {
        private final VirtualFile myFile;

        private ClearThisFileModeAction(@Nullable VirtualFile file) {
            super("<Clear>", "Clear " +
                    (file == null ? "default" : "file '" + file.getName() + "'") +
                    " mode.", null);
            myFile = file;
        }

        public void actionPerformed(final AnActionEvent e) {
            chosen(myFile, FileMode.defaultMode());
        }
    }

    protected abstract void chosen(VirtualFile virtualFile, FileMode fileMode);

    public DefaultActionGroup createGroup(boolean showClear) {
        DefaultActionGroup group = new DefaultActionGroup();

        //FileMode mode = myVirtualFile == null ? null : modeFromFile(myVirtualFile);

        if (showClear) {
            group.add(new ClearThisFileModeAction(myVirtualFile));
        }

        fileModeActions(group, myVirtualFile, FileMode.all());

//        DefaultActionGroup more = new DefaultActionGroup("more", true);
//        group.add(more);
//        fileModeActions(more, myVirtualFile, FileMode.all());
        return group;
    }
}
