/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashModuleSettingsComponent.java, Class: BashModuleSettingsComponent
 * Last modified: 2010-02-12
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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: Feb 11, 2010
 * Time: 8:52:02 PM
 */
public class BashModuleSettingsComponent implements ModuleComponent, ModuleConfigurationEditorProvider {
    private final Module myModule;
    private BashModuleSettingsStorage myModuleSettingsStorage;

    public BashModuleSettingsComponent(final Module module, final BashModuleSettingsStorage mduleStorage) {
        myModule = module;
        myModuleSettingsStorage = BashModuleSettingsStorage.getInstance(module);
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    public void moduleAdded() {
    }

    @NotNull
    public String getComponentName() {
        return "BashModule";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState moduleConfigurationState) {
        return new ModuleConfigurationEditor[0];
    }
}
