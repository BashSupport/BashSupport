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
import com.intellij.notification.NotificationType;
import com.intellij.notification.impl.NotificationFullContent;

/**
 * @author jansorg
 */
class PluginCompatibilityNotification extends Notification implements NotificationFullContent {

    PluginCompatibilityNotification() {
        super("BashSupport", BashIcons.BASH_FILE_ICON, "Plugin incompatibility detected", null,
                "JetBrains is bundling a new Shell plugin with 2019.2." +
                        "<br><em>BashSupport</em> supports more advanced features. <em>Shell</em> is more robust and providing better integration of basic functionality." +
                        "<br><br>You shouldn't use BashSupport and Shell simultaneously. <b>Make sure to disable one of them.</b>",
                NotificationType.INFORMATION, null);

        addAction(new DisablePluginAction("JetBrains Shell", PluginManagerUtil.SHELL_ID, this));
        addAction(new DisablePluginAction("BashSupport", PluginManagerUtil.BASHSUPPORT_ID, this));
    }
}
