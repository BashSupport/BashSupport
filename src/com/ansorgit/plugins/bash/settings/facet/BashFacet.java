/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFacet.java, Class: BashFacet
 * Last modified: 2010-02-18
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

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

public class BashFacet extends Facet<BashFacetConfiguration> {
    public BashFacet(@NotNull FacetType facetType, @NotNull Module module, @NotNull String name, @NotNull BashFacetConfiguration configuration, Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    /*@Override
    public boolean isDisposed() {
        return super.isDisposed();
    } */

    @Override
    public void initFacet() {
        super.initFacet();
    }

    @Override
    public void disposeFacet() {
        super.disposeFacet();
    }

    public static BashFacet getInstance(@NotNull Module module) {
        return FacetManager.getInstance(module).getFacetByType(BashFacetType.ID);
    }
}