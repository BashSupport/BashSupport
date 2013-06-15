/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: RemoveHighlightingPass.java, Class: RemoveHighlightingPass
 * Last modified: 2011-04-30 16:33
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

/*
 */

package com.ansorgit.plugins.bash.editor.highlighting.codeHighlighting;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
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
 * Text editor highlighting pass which removes highlighting from heredocs.
 * The parser marks heredocs, that's why keyword tokens may appear in heredocs.
 * The token remapper of the parser fixes this for the parsing process, but
 * the highlighting appeareantly is based on the lexing result.
 * <p/>
 * In this highlighting pass we remove all highlighting in heredoc subtokens.
 */
class RemoveHighlightingPass extends TextEditorHighlightingPass {
    private final BashFile bashFile;
    private final Editor editor;
    private List<TextRange> unhighlightHeredocRanges;
    private List<TextRange> unhighlightUnusedFormat;

    public RemoveHighlightingPass(Project project, BashFile bashFile, Editor editor) {
        super(project, editor.getDocument(), true);
        this.bashFile = bashFile;
        this.editor = editor;
    }

    @Override
    public void doCollectInformation(ProgressIndicator progress) {
        final List<TextRange> collectedRanges = Lists.newLinkedList();
        final List<TextRange> collectedUnused = Lists.newLinkedList();

        PsiRecursiveElementVisitor visitor = new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof BashHereDoc) {
                    collectedRanges.add(element.getTextRange());
                } else if (element instanceof BashFunctionDef) {
                    if (HighlightingKeys.IS_UNUSED.get(element, Boolean.FALSE) == Boolean.FALSE) {
                        collectedUnused.add(element.getTextRange());
                    }
                } else {
                    element.acceptChildren(this);
                }
            }
        };

        visitor.visitElement(bashFile);
        unhighlightHeredocRanges = collectedRanges;
        unhighlightUnusedFormat = collectedUnused;
    }

    @Override
    public void doApplyInformationToEditor() {
        if (unhighlightHeredocRanges != null) {
            for (TextRange r : unhighlightHeredocRanges) {
                editor.getMarkupModel().addRangeHighlighter(r.getStartOffset(), r.getEndOffset(),
                        HighlighterLayer.ADDITIONAL_SYNTAX, TextAttributes.ERASE_MARKER, HighlighterTargetArea.LINES_IN_RANGE);
            }

            unhighlightHeredocRanges = null;
        }
    }
}
