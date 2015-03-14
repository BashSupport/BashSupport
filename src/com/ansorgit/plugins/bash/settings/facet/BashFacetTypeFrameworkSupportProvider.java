/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFacetTypeFrameworkSupportProvider.java, Class: BashFacetTypeFrameworkSupportProvider
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

package com.ansorgit.plugins.bash.settings.facet;

import com.intellij.facet.ui.FacetBasedFrameworkSupportProvider;
import com.intellij.ide.util.frameworkSupport.FrameworkVersion;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.annotations.NotNull;

public class BashFacetTypeFrameworkSupportProvider extends FacetBasedFrameworkSupportProvider<BashFacet> {
    protected BashFacetTypeFrameworkSupportProvider() {
        super(BashFacetType.INSTANCE);
    }

    @Override
    public String getTitle() {
        return "BashSupport";
    }

    @Override
    protected void setupConfiguration(BashFacet bashFacet, ModifiableRootModel modifiableRootModel, FrameworkVersion frameworkVersion) {

    }

    @Override
    public boolean isEnabledForModuleBuilder(@NotNull ModuleBuilder builder) {
        return true;
    }

    @Override
    public boolean isEnabledForModuleType(@NotNull ModuleType moduleType) {
        return true;
    }
}