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

package com.ansorgit.plugins.bash.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

/**
 * @author jansorg
 */
@State(name = "BashSupportProjectSettings", storages = {@Storage("bashsupport_project.xml")})
public class BashProjectSettingsComponent implements PersistentStateComponent<BashProjectSettings>, ProjectComponent {
    private BashProjectSettings settings = new BashProjectSettings();

    public BashProjectSettings getState() {
        return settings;
    }

    public void loadState(@NotNull BashProjectSettings state) {
        this.settings = state;
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NotNull
    public String getComponentName() {
        return "BashSupportProject";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
        this.settings = null;
    }
}
