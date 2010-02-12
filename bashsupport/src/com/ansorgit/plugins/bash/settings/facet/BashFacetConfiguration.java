/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFacetConfiguration.java, Class: BashFacetConfiguration
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

package com.ansorgit.plugins.bash.settings.facet;

import com.ansorgit.plugins.bash.settings.facet.ui.BashFacetUI;
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;

@State(
        name = "BashFacetConfiguration",
        storages = {
                @Storage(
                        id = "default",
                        file = "$MODULE_FILE$"
                )
        }
)
public class BashFacetConfiguration implements FacetConfiguration, PersistentStateComponent<BashFacetSettings> {
    private BashFacetSettings settings;

    public FacetEditorTab[] createEditorTabs(FacetEditorContext facetEditorContext, FacetValidatorsManager facetValidatorsManager) {
        return new FacetEditorTab[]{
                new BashFacetUI(this, facetEditorContext, facetValidatorsManager)
        };
    }

    @Deprecated
    public void readExternal(Element element) throws InvalidDataException {
    }

    @Deprecated
    public void writeExternal(Element element) throws WriteExternalException {
    }

    public BashFacetSettings getState() {
        return settings;
    }

    public void loadState(BashFacetSettings bashFacetSettings) {
        this.settings = bashFacetSettings;
    }
}