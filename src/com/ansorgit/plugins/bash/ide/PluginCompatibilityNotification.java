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

import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.impl.NotificationFullContent;

/**
 * @author jansorg
 */
class PluginCompatibilityNotification extends Notification implements NotificationFullContent {

    PluginCompatibilityNotification(NotificationGroup group) {
        super(group.getDisplayId(), BashIcons.BASH_FILE_ICON, "Conflicting plugins detected", null,
                "You have two plugins installed:<ul style=\"margin-left:20px;list-style:circle;\">" +
                        "<li>The new JetBrains Shell plugin is bundled with IntelliJ IDEA 2019.2 and later. The plugin is very robust and provides better integration with basic functionality.</li>" +
                        "<li>BashSupport is not as light-weight and robust but offers more features.</li>" +
                        "</ul>Select one of the plugins to continue.",
                NotificationType.INFORMATION, null);

        addAction(new DisablePluginAction("Use Shell Support", PluginManagerUtil.BASHSUPPORT_ID, this));
        addAction(new DisablePluginAction("Use BashSupport", PluginManagerUtil.SHELL_ID, this));
    }
}
