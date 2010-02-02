/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSettingsConfigurable.java, Class: BashSettingsConfigurable
 * Last modified: 2009-12-04
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

package com.ansorgit.plugins.bash.settings;

import com.ansorgit.plugins.bash.lang.BashLoader;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Date: 12.05.2009
 * Time: 18:51:48
 *
 * @author Joachim Ansorg
 */
public class BashSettingsConfigurable implements ApplicationComponent, Configurable {
    private BashSettingsPane settingsPanel;

    @NotNull
    public String getComponentName() {
        return BashLoader.COMPONENT_NAME + ".SettingsConfigurable";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
        if (settingsPanel != null) {
            this.settingsPanel.dispose();
            this.settingsPanel = null;
        }
    }

    @Nls
    public String getDisplayName() {
        return "BashSupport";
    }

    public Icon getIcon() {
        return BashIcons.BASH_LARGE_ICON;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (settingsPanel == null) {
            settingsPanel = new BashSettingsPane();
        }

        return settingsPanel.getPanel();
    }

    public boolean isModified() {
        if (settingsPanel == null) return false;

        return settingsPanel.isModified(BashSettings.storedSettings());
    }

    public void apply() throws ConfigurationException {
        settingsPanel.storeSettings(BashSettings.storedSettings());
    }

    public void reset() {
        settingsPanel.setData(BashSettings.storedSettings());
    }

    public void disposeUIResources() {
    }
}
