/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashProjectSettingsComponent.java, Class: BashProjectSettingsComponent
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

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: Oct 30, 2009
 * Time: 9:12:54 PM
 */
@State(
        name = "BashSupportProjectSettings",
        storages = {
                @Storage(id = "default",
                        file = "$PROJECT_FILE$"),
                @Storage(id = "dir",
                        file = "$PROJECT_CONFIG_DIR$/bashsupport_project.xml",
                        scheme = StorageScheme.DIRECTORY_BASED)}
)
public class BashProjectSettingsComponent implements PersistentStateComponent<BashProjectSettings>, ProjectComponent {
    private BashProjectSettings settings = new BashProjectSettings();

    @Override
    public BashProjectSettings getState() {
        return settings;
    }

    @Override
    public void loadState(BashProjectSettings state) {
        this.settings = state;
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "BashSupportProject";
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }
}
