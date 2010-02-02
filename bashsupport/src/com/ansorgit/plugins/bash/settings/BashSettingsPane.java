/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSettingsPane.java, Class: BashSettingsPane
 * Last modified: 2010-01-29
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

import com.intellij.openapi.Disposable;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Date: 12.05.2009
 * Time: 18:53:43
 *
 * @author Joachim Ansorg
 */
public class BashSettingsPane implements Disposable {
    private JCheckBox loadFilesWithoutExtension;
    private JPanel panel;
    private JCheckBox autocompleteBuiltinVars;
    private JCheckBox autocompleteBuiltinCommands;
    private JCheckBox guessByContent;

    public BashSettingsPane() {
        loadFilesWithoutExtension.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                guessByContent.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
    }

    public void dispose() {
    }

    public void setData(BashSettings settings) {
        loadFilesWithoutExtension.setSelected(settings.isLoadEmptyExtensions());
        guessByContent.setSelected(settings.isGuessByContent());
        autocompleteBuiltinVars.setSelected(settings.isAutocompleteBuiltinVars());
        autocompleteBuiltinCommands.setSelected(settings.isAutocompleteBuiltinCommands());
    }

    public void storeSettings(BashSettings settings) {
        settings.setLoadEmptyExtensions(loadFilesWithoutExtension.isSelected());
        settings.setGuessByContent(guessByContent.isSelected());
        settings.setAutocompleteBuiltinVars(autocompleteBuiltinVars.isSelected());
        settings.setAutocompleteBuiltinCommands(autocompleteBuiltinCommands.isSelected());
    }

    public boolean isModified(BashSettings settings) {
        return settings.isLoadEmptyExtensions() != loadFilesWithoutExtension.isSelected()
                || settings.isGuessByContent() != guessByContent.isSelected()
                || settings.isAutocompleteBuiltinVars() != autocompleteBuiltinVars.isSelected()
                || settings.isAutocompleteBuiltinCommands() != autocompleteBuiltinCommands.isSelected();
    }

    public JPanel getPanel() {
        return panel;
    }
}
