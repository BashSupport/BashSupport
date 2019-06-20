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
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.notification.Notification;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An action to disable a particular plugin.
 * It warns when there are plugins depending on the disabled plugin before disabling it.
 * After successfully disabling the plugin this actions asks whether the IDE should be restarted to activate the changes.
 *
 * @author jansorg
 */
class DisablePluginAction extends AnAction {
    private final Notification notification;
    private final String pluginId;

    DisablePluginAction(@NotNull String name, @NotNull String pluginId, @NotNull Notification notification) {
        super("Disable " + name);
        this.notification = notification;
        this.pluginId = pluginId;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        // return early if the plugin isn't available or already disabled
        IdeaPluginDescriptor descriptor = PluginManagerUtil.findDescriptor(pluginId);
        if (descriptor == null || !descriptor.isEnabled()) {
            return;
        }

        List<IdeaPluginDescriptor> deps = findPluginsWithHardDependencyOn(descriptor);
        if (!deps.isEmpty()) {
            String message = deps.size() == 1
                    ? String.format("Plugin %s depends on %s. Would you really like to disable it?", deps.get(0).getName(), descriptor.getName())
                    : String.format("%d plugins depend on %s. Would you really like to disable it?", deps.size(), descriptor.getName());

            if (Messages.showYesNoDialog(project, message, "Disable Dependency", Messages.getQuestionIcon()) == Messages.NO) {
                return;
            }
        }

        notification.expire();

        // this is necessary to update the status in the Settings
        // The project table model of the settings uses the enabled property
        descriptor.setEnabled(false);

        PluginManagerCore.disablePlugin(pluginId);

        Application app = ApplicationManager.getApplication();
        if (app.isRestartCapable()) {
            if (Messages.showYesNoDialog(project, "Would you like to restart to activate the changes?", "Restart", Messages.getQuestionIcon()) == Messages.YES) {
                app.restart();
            }
        }
    }

    /**
     * Collects all enabled plugins which depend on the given plugin.
     * @param query The target plugin
     * @return A non-null list of plugin descriptors
     */
    @NotNull
    private static List<IdeaPluginDescriptor> findPluginsWithHardDependencyOn(IdeaPluginDescriptor query) {
        PluginId queryId = query.getPluginId();

        List<IdeaPluginDescriptor> dependants = Lists.newArrayList();
        for (IdeaPluginDescriptor plugin : PluginManagerCore.getPlugins()) {
            if (plugin.isEnabled() && isDependency(plugin, queryId) && !isOptionalDependency(plugin, queryId)) {
                dependants.add(plugin);
            }
        }
        return dependants;
    }

    private static boolean isDependency(IdeaPluginDescriptor plugin, PluginId target) {
        for (PluginId dependency : plugin.getDependentPluginIds()) {
            if (dependency.compareTo(target) == 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean isOptionalDependency(IdeaPluginDescriptor plugin, PluginId target) {
        for (PluginId dependency : plugin.getOptionalDependentPluginIds()) {
            if (dependency.compareTo(target) == 0) {
                return true;
            }
        }
        return false;
    }
}
