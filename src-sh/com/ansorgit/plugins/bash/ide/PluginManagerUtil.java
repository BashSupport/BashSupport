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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author jansorg
 */
final class PluginManagerUtil {
    static final String SHELL_ID = "com.jetbrains.sh";
    static final String BASHSUPPORT_ID = "BashSupport";

    private PluginManagerUtil() {
    }

    /**
     * Locates the plugin descriptor for a given plugin id.
     *
     * @param pluginId The queried plugin
     * @return The locator or {@code null}, if it couldn't be found.
     */
    @Nullable
    static IdeaPluginDescriptor findDescriptor(@NotNull String pluginId) {
        for (IdeaPluginDescriptor descriptor : PluginManagerCore.getPlugins()) {
            if (pluginId.equals(descriptor.getPluginId().getIdString())) {
                return descriptor;
            }
        }
        return null;
    }
}
