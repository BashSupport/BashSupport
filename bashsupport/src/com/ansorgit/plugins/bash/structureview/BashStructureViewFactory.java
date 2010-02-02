/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashStructureViewFactory.java, Class: BashStructureViewFactory
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

package com.ansorgit.plugins.bash.structureview;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 12.04.2009
 * Time: 20:25:32
 *
 * @author Joachim Ansorg
 */
public class BashStructureViewFactory implements PsiStructureViewFactory {
    public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
        return new TreeBasedStructureViewBuilder() {
            @NotNull
            public StructureViewModel createStructureViewModel() {
                return new BashStructureViewModel(psiFile);
            }

            public boolean isRootNodeShown() {
                return false;
            }
        };
    }
}
