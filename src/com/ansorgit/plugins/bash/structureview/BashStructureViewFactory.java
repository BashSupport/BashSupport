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

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Creates the structure view builder for Bash files.
 * <br>
 * @author jansorg
 */
public class BashStructureViewFactory implements PsiStructureViewFactory {
    public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
        return new BashTreeBasedStructureViewBuilder(psiFile);
    }

    private static class BashTreeBasedStructureViewBuilder extends TreeBasedStructureViewBuilder {
        private final PsiFile psiFile;

        BashTreeBasedStructureViewBuilder(PsiFile psiFile) {
            this.psiFile = psiFile;
        }

        @NotNull
        @Override
        public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
            return new BashStructureViewModel(editor, psiFile);
        }

        public boolean isRootNodeShown() {
            return false;
        }
    }
}
