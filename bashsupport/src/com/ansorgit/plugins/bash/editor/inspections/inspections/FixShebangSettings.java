/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: FixShebangSettings.java, Class: FixShebangSettings
 * Last modified: 2013-05-09
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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import javax.swing.*;

/**
 * These are the ui settings for the fix shebang inspection.
 */
public class FixShebangSettings {
    private JTextArea validCommandsEdit;
    private JPanel settingsPanel;

    public JPanel getSettingsPanel() {
        return settingsPanel;
    }

    public JTextArea getValidCommandsTextArea() {
        return validCommandsEdit;
    }
}
