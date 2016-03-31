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

package com.ansorgit.plugins.bash.structureview;

import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author jansorg
 */
class BashStructureViewModel extends TextEditorBasedStructureViewModel implements StructureViewModel {
    private static final Class[] CLASSS = new Class[]{BashFunctionDef.class};
    private static final Sorter[] SORTERS = new Sorter[]{Sorter.ALPHA_SORTER};

    private final PsiFile myFile;

    BashStructureViewModel(Editor editor, PsiFile file) {
        super(editor, file);
        this.myFile = file;
    }

    protected PsiFile getPsiFile() {
        return myFile;
    }

    @NotNull
    public StructureViewTreeElement getRoot() {
        return new BashStructureViewElement(myFile);
    }

    @NotNull
    public Grouper[] getGroupers() {
        return Grouper.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    protected Class[] getSuitableClasses() {
        return CLASSS;
    }

    @NotNull
    public Sorter[] getSorters() {
        return SORTERS;
    }

    @NotNull
    public Filter[] getFilters() {
        return Filter.EMPTY_ARRAY;
    }
}
