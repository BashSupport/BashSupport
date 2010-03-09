/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashEditorHighlighter.java, Class: BashEditorHighlighter
 * Last modified: 2010-03-09
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

package com.ansorgit.plugins.bash.editor.highlighting;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * User: jansorg
 * Date: Mar 9, 2010
 * Time: 9:15:59 PM
 */
public class BashEditorHighlighter extends LayeredLexerEditorHighlighter {
    public BashEditorHighlighter(EditorColorsScheme scheme, Project project, VirtualFile virtualFile) {
        super(new BashSyntaxHighlighter(), scheme);
        //registerBashHereDocHighlighter();
    }

    /*private void registerBashHereDocHighlighter() {
      SyntaxHighlighter plainHighlighter = new PlainSyntaxHighlighter();
      final LayerDescriptor hereDocLayer = new LayerDescriptor(plainHighlighter, "\n", BashSyntaxHighlighter.HERE_DOC);
      registerLayer(BashElementTypes.HEREDOC_ELEMENT, hereDocLayer);
    } */
}
