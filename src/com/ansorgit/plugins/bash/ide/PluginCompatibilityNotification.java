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
import com.intellij.notification.*;
import com.intellij.notification.impl.NotificationFullContent;

/**
 * @author jansorg
 */
class PluginCompatibilityNotification extends Notification implements NotificationFullContent {

    PluginCompatibilityNotification(NotificationGroup group) {
        super(group.getDisplayId(), BashIcons.BASH_FILE_ICON, "BashSupport: Conflicting plugins", null,
                "You have two plugins installed:<br>" +
                        "–&nbsp;The JetBrains Shell plugin is bundled with IntelliJ IDEA 2019.2 and later. It's very robust and provides better integration with basic functionality.<br>" +
                        "–&nbsp;BashSupport is not as light-weight and robust but offers more features.<br>" +
                        "–&nbsp;<a href=\"https://www.bashsupport.com/plugin-comparison/\">Open a comparision</a><br><br>" +
                        "<em>Select one of the plugins to continue.</em>",
                NotificationType.INFORMATION, new NotificationListener.UrlOpeningListener(false));

        addAction(new DisablePluginAction("Use Shell Support", PluginManagerUtil.BASHSUPPORT_ID, this));
        addAction(new DisablePluginAction("Use BashSupport", PluginManagerUtil.SHELL_ID, this));
    }
}
