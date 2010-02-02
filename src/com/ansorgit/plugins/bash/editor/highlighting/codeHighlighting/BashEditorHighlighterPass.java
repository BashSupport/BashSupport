/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashEditorHighlighterPass.java, Class: BashEditorHighlighterPass
 * Last modified: 2010-01-25
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

/*
 */

package com.ansorgit.plugins.bash.editor.highlighting.codeHighlighting;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 * User: jansorg
 * Date: Jan 25, 2010
 * Time: 8:36:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BashEditorHighlighterPass extends TextEditorHighlightingPass {
    private final BashFile bashFile;

    public BashEditorHighlighterPass(Project project, BashFile bashFile, Editor editor) {
        super(project, editor.getDocument(), true);
        this.bashFile = bashFile;
    }

    @Override
    public void doCollectInformation(ProgressIndicator progress) {
        //fixme add highlighting of bash variables inside of strings
    }

    @Override
    public void doApplyInformationToEditor() {

    }
}
