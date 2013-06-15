/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: RemoveHighlightingPassFactory.java, Class: RemoveHighlightingPassFactory
 * Last modified: 2011-03-31 20:06
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

package com.ansorgit.plugins.bash.editor.highlighting.codeHighlighting;

import com.ansorgit.plugins.bash.BashComponents;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Factory which provides text editor highlighters for the Bash file type.
 */
public class RemoveHighlightingPassFactory extends AbstractProjectComponent implements TextEditorHighlightingPassFactory {
    private TextEditorHighlightingPassRegistrar myRegistrar;

    protected RemoveHighlightingPassFactory(Project project, TextEditorHighlightingPassRegistrar highlightingPassRegistrar) {
        super(project);
        myRegistrar = highlightingPassRegistrar;
    }

    public void projectOpened() {
        myRegistrar.registerTextEditorHighlightingPass(this, TextEditorHighlightingPassRegistrar.Anchor.AFTER, HighlighterLayer.ADDITIONAL_SYNTAX, false, true);
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return BashComponents.RemoveHighlighterFactory;
    }

    public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor) {
        if (file instanceof BashFile) {
            return new RemoveHighlightingPass(file.getProject(), (BashFile) file, editor);
        }

        return null;
    }
}
