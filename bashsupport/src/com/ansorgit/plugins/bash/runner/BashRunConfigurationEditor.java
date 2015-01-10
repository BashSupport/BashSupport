/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashRunConfigurationEditor.java, Class: BashRunConfigurationEditor
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.runner;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BashRunConfigurationEditor extends SettingsEditor<BashRunConfiguration> {
    private BashConfigForm form;

    public BashRunConfigurationEditor(Module module) {
        this.form = new BashConfigForm();
        this.form.setModuleContext(module);
    }

    @Override
    protected void resetEditorFrom(BashRunConfiguration runConfiguration) {
        form.reset(runConfiguration);
        form.resetBash(runConfiguration);
    }

    @Override
    protected void applyEditorTo(BashRunConfiguration runConfiguration) throws ConfigurationException {
        form.applyTo(runConfiguration);
        form.applyBashTo(runConfiguration);
    }

    @Override
    @NotNull
    protected JComponent createEditor() {
        return form;
    }

    @Override
    protected void disposeEditor() {
        form = null;
    }
}