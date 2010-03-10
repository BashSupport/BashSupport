/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: HeredocVarHighlightingFactory.java, Class: HeredocVarHighlightingFactory
 * Last modified: 2010-03-10
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

package com.ansorgit.plugins.bash.editor.highlighting.codeHighlighting;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: Mar 10, 2010
 * Time: 7:38:00 PM
 */
class HeredocVarHighlightingFactory implements TextEditorHighlightingPassFactory {
    public TextEditorHighlightingPass createHighlightingPass(@NotNull PsiFile file, @NotNull Editor editor) {
        if (file instanceof BashFile) {
            return new AddHeredocVarHighlightingPass(file.getProject(), (BashFile) file, editor);
        }

        return null;
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }

    @NotNull
    public String getComponentName() {
        return null;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }
}
