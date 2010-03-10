/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: AddHeredocVarHighlightingPass.java, Class: AddHeredocVarHighlightingPass
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

/*
 */

package com.ansorgit.plugins.bash.editor.highlighting.codeHighlighting;

import com.ansorgit.plugins.bash.editor.highlighting.BashSyntaxHighlighter;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.google.common.collect.Lists;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;

import java.util.List;

/**
 * User: jansorg
 * Date: Jan 25, 2010
 * Time: 8:36:58 PM
 */
class AddHeredocVarHighlightingPass extends TextEditorHighlightingPass {
    private final BashFile bashFile;
    private final Editor editor;
    private List<Pair<Boolean, TextRange>> varHighlightRanges;

    public AddHeredocVarHighlightingPass(Project project, BashFile bashFile, Editor editor) {
        super(project, editor.getDocument(), true);
        this.bashFile = bashFile;
        this.editor = editor;
    }

    @Override
    public void doCollectInformation(ProgressIndicator progress) {
        final List<Pair<Boolean, TextRange>> varRanges = Lists.newLinkedList();

        PsiRecursiveElementVisitor visitor = new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof BashHereDoc) {
                    BashHereDoc doc = (BashHereDoc) element;
                    ASTNode docNode = doc.getNode();

                    boolean evaluatingVariables = doc.isEvaluatingVariables();

                    //if were in highlighting mode only remove highlighting from the other areas
                    ASTNode[] vars = docNode != null ? docNode.getChildren(BashTokenTypes.variableSet) : ASTNode.EMPTY_ARRAY;
                    if (vars != null && vars.length > 0) {
                        for (ASTNode var : vars) {
                            TextRange currentRange = var.getTextRange();
                            varRanges.add(Pair.create(evaluatingVariables, currentRange));
                        }
                    }
                } else {
                    element.acceptChildren(this);
                }
            }
        };

        visitor.visitElement(bashFile);
        varHighlightRanges = varRanges;
    }

    @Override
    public void doApplyInformationToEditor() {
        for (Pair<Boolean, TextRange> r : varHighlightRanges) {
            TextRange range = r.second;
            TextAttributes textAttributes = r.first ? BashSyntaxHighlighter.VAR_USE_ATTRIB : TextAttributes.ERASE_MARKER;

            editor.getMarkupModel().addRangeHighlighter(range.getStartOffset(), range.getEndOffset(),
                    HighlighterLayer.LAST, textAttributes, HighlighterTargetArea.EXACT_RANGE);
        }
    }
}