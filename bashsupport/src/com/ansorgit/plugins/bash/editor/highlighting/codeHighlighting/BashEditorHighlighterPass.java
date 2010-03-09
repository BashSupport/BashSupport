/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashEditorHighlighterPass.java, Class: BashEditorHighlighterPass
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

/*
 */

package com.ansorgit.plugins.bash.editor.highlighting.codeHighlighting;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.google.common.collect.Lists;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;

import java.util.List;

/**
 * User: jansorg
 * Date: Jan 25, 2010
 * Time: 8:36:58 PM
 */
public class BashEditorHighlighterPass extends TextEditorHighlightingPass {
    private final BashFile bashFile;
    private final Editor editor;
    private final List<TextRange> docRanges = Lists.newArrayList();

    public BashEditorHighlighterPass(Project project, BashFile bashFile, Editor editor) {
        super(project, editor.getDocument(), false);
        this.bashFile = bashFile;
        this.editor = editor;
    }

    @Override
    public void doCollectInformation(ProgressIndicator progress) {
        //fixme take care of proper variable highlighting in here docs
        PsiRecursiveElementVisitor visitor = new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof BashHereDoc) {
                    docRanges.add(element.getTextRange());
                }

                element.acceptChildren(this);
            }
        };

        visitor.visitElement(bashFile);
    }

    @Override
    public void doApplyInformationToEditor() {
        for (TextRange r : docRanges) {
            editor.getMarkupModel().addRangeHighlighter(r.getStartOffset(), r.getEndOffset(), HighlighterLayer.LAST, TextAttributes.ERASE_MARKER, HighlighterTargetArea.LINES_IN_RANGE);
        }
    }
}
