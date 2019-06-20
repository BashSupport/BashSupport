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

package com.ansorgit.plugins.bash.ide;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.notification.NotificationsManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.sh.ShFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Project component which registers a new listener to show a message when
 * a file is opened with the Shell plugin.
 * <p>
 * This component must be used in a way so that Shell is an optional dependency.
 *
 * @author jansorg
 */
public class ShellEditorMessageComponent implements ProjectComponent, FileEditorManagerListener, Disposable {
    private final Project project;

    public ShellEditorMessageComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        StartupManager.getInstance(project).runWhenProjectIsInitialized(() -> {
            project.getMessageBus().connect(this).subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);
        });
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        if (!ShFileType.INSTANCE.equals(file.getFileType())) {
            return;
        }

        // make sure that we only display one message,
        // this could happen at project startup, for example
        NotificationsManager mgr = NotificationsManager.getNotificationsManager();
        PluginCompatibilityNotification[] existing = mgr.getNotificationsOfType(PluginCompatibilityNotification.class, project);
        if (existing.length != 0) {
            return;
        }

        // don't show a message if the plugin was already deactivated and the IDE wasn't restarted yet
        // either BashSupport or Shell could have been disabled
        IdeaPluginDescriptor shell = PluginManagerUtil.findDescriptor(PluginManagerUtil.SHELL_ID);
        IdeaPluginDescriptor bashsupport = PluginManagerUtil.findDescriptor(PluginManagerUtil.BASHSUPPORT_ID);
        if (shell == null || !shell.isEnabled() || bashsupport == null || !bashsupport.isEnabled()) {
            return;
        }

        // finally show the notification
        new PluginCompatibilityNotification().notify(project);
    }

    @Override
    public void dispose() {
    }
}
