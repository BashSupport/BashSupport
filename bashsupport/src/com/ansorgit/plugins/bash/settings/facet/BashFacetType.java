/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFacetType.java, Class: BashFacetType
 * Last modified: 2010-02-11
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

import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BashFacetType extends FacetType<BashFacet, BashFacetConfiguration> {
    public static final FacetTypeId<BashFacet> ID = new FacetTypeId<BashFacet>("bash");
    public static final BashFacetType INSTANCE = new BashFacetType();

    public BashFacetType() {
        super(ID, "bash", "BashSupport");
    }

    @Override
    public BashFacetConfiguration createDefaultConfiguration() {
        return new BashFacetConfiguration();
    }

    @Override
    public BashFacet createFacet(@NotNull Module module, String name,
                                 @NotNull BashFacetConfiguration configuration,
                                 @Nullable Facet underlyingFacet) {
        return new BashFacet(this, module, name, configuration, underlyingFacet);
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
        return true;
    }

    @Override
    public Icon getIcon() {
        return BashIcons.BASH_FILE_ICON;
    }
}