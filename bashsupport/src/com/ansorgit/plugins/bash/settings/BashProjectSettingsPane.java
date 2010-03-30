/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashProjectSettingsPane.java, Class: BashProjectSettingsPane
 * Last modified: 2010-03-30
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

import com.intellij.openapi.util.text.StringUtil;
import org.picocontainer.Disposable;

import javax.swing.*;
import java.util.*;

/**
 * User: jansorg
 * Date: Oct 30, 2009
 * Time: 9:18:52 PM
 */
public class BashProjectSettingsPane implements Disposable {
    private JPanel settingsPane;
    private JTextArea globalVarList;
    private JCheckBox globalVarAutocompletion;
    private JCheckBox bash4Support;
    private JCheckBox autocompleteInternalVars;
    private JCheckBox autocompleteInternalCommands;
    private JCheckBox enableFormatterCheckbox;

    public BashProjectSettingsPane() {
    }

    public void dispose() {
    }

    public void setData(BashProjectSettings settings) {
        globalVarAutocompletion.setSelected(settings.isAutcompleteGlobalVars());
        globalVarList.setText(joinGlobalVarList(settings.getGlobalVariables()));
        bash4Support.setSelected(settings.isSupportBash4());
        autocompleteInternalCommands.setSelected(settings.isAutocompleteBuiltinCommands());
        autocompleteInternalVars.setSelected(settings.isAutocompleteBuiltinVars());
        enableFormatterCheckbox.setSelected(settings.isFormatterEnabled());
    }

    public void storeSettings(BashProjectSettings settings) {
        settings.setAutcompleteGlobalVars(globalVarAutocompletion.isSelected());
        settings.setGlobalVariables(splitGlobalVarList(globalVarList.getText()));
        settings.setSupportBash4(bash4Support.isSelected());
        settings.setAutocompleteBuiltinCommands(autocompleteInternalCommands.isSelected());
        settings.setAutocompleteBuiltinVars(autocompleteInternalVars.isSelected());
        settings.setFormatterEnabled(enableFormatterCheckbox.isSelected());
    }

    public boolean isModified(BashProjectSettings settings) {
        return settings.isAutcompleteGlobalVars() != globalVarAutocompletion.isSelected() ||
                !joinGlobalVarList(settings.getGlobalVariables()).equals(globalVarList.getText()) ||
                bash4Support.isSelected() != settings.isSupportBash4() ||
                autocompleteInternalVars.isSelected() != settings.isAutocompleteBuiltinVars() ||
                autocompleteInternalCommands.isSelected() != settings.isAutocompleteBuiltinCommands() ||
                enableFormatterCheckbox.isSelected() != settings.isFormatterEnabled();
    }

    public JPanel getPanel() {
        return settingsPane;
    }

    private String joinGlobalVarList(Collection<String> data) {
        return StringUtil.join(data, "\n");
    }

    private Set<String> splitGlobalVarList(String data) {
        if (data.length() == 0) {
            return Collections.emptySet();
        }
        return new HashSet<String>(Arrays.asList(data.split("\\n").clone()));
    }
}
