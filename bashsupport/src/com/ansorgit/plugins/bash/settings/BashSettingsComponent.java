/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSettingsComponent.java, Class: BashSettingsComponent
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

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

/**
 * Date: 12.05.2009
 * Time: 18:45:41
 *
 * @author Joachim Ansorg
 */
@State(
        name = "BashSupportSettings",
        storages = {
                @Storage(
                        id = "BashSupportSettings",
                        file = "$APP_CONFIG$/bashsupport.xml"
                )}
)
public class BashSettingsComponent implements PersistentStateComponent<BashSettings> {
    private BashSettings settings = new BashSettings();

    public BashSettingsComponent() {
    }

    public BashSettings getState() {
        return settings;
    }

    public void loadState(BashSettings state) {
        this.settings = state;
    }
}
