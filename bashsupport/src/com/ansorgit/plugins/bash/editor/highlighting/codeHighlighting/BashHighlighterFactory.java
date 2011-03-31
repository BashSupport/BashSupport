/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashHighlighterFactory.java, Class: BashHighlighterFactory
 * Last modified: 2010-03-24
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

import com.ansorgit.plugins.bash.BashComponents;
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Factory which provides text editor highlighters for the Bash file type.
 * <p/>
 * User: jansorg
 * Date: Jan 25, 2010
 * Time: 8:27:58 PM
 */
public class BashHighlighterFactory implements ProjectComponent {
    private TextEditorHighlightingPassRegistrar myRegistrar;

    public BashHighlighterFactory(final TextEditorHighlightingPassRegistrar passRegistrar) {
        myRegistrar = passRegistrar;
    }

    public void projectOpened() {
        myRegistrar.registerTextEditorHighlightingPass(new RemoveHighlightingFactory(), TextEditorHighlightingPassRegistrar.Anchor.AFTER, HighlighterLayer.ADDITIONAL_SYNTAX, false, true);
    }

    public void projectClosed() {
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return BashComponents.HighlighterFactory;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }
}
