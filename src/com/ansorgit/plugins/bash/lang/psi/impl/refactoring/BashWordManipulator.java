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

package com.ansorgit.plugins.bash.lang.psi.impl.refactoring;

import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.util.BashStringUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Manipulator for BashWord PSI elements.
 * Needed to compute the value range, which is used by the language injection feature of IntelliJ.
 *
 * @author jansorg
 */
public class BashWordManipulator implements ElementManipulator<BashWord> {
    private static final char[] DOLLAR_IGNORED = new char[]{'$'};

    @Override
    public BashWord handleContentChange(@NotNull BashWord element, @NotNull TextRange textRange, String contentForRange) throws IncorrectOperationException {
        String oldContent = element.getText();
        String newContent = oldContent.substring(0, textRange.getStartOffset()) + contentForRange + oldContent.substring(textRange.getEndOffset());

        //If the next content contains characters which need to be escaped, handle this in the content range
        int contentStart = newContent.indexOf('\'') + 1;
        int contentEnd = newContent.lastIndexOf('\'');
        if (contentStart > contentEnd) {
            throw new IncorrectOperationException("Invalid content change");
        }

        if (newContent.startsWith("$'") && newContent.indexOf('\\', contentStart) < contentEnd) {
            //contains backslash characters in the content which need to be escaped
            String toEscape = newContent.substring(contentStart, contentEnd);
            newContent = "$'" + BashStringUtils.escape(toEscape, '\\', DOLLAR_IGNORED) + "'";
            contentEnd = newContent.length() - 1;
        }

        if (newContent.indexOf('\'', contentStart) < contentEnd) {
            //contains string markers in the content which need to be escaped
            String toEscape = newContent.substring(contentStart, contentEnd);
            newContent = "$'" + BashStringUtils.escape(toEscape, '\'') + "'";
        }

        //PsiElement newElement = BashPsiElementFactory.createWord(element.getProject(), newContent);
        //assert newElement instanceof BashWord : "Element created for text not a word: " + newContent;

        ASTNode valueNode = element.getNode().getFirstChildNode();
        assert valueNode instanceof LeafElement;
        ((LeafElement)valueNode).replaceWithText(newContent);

        return element;

        //return BashPsiUtils.replaceElement(element, newElement);
    }

    @Override
    public BashWord handleContentChange(@NotNull BashWord element, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, TextRange.create(0, element.getTextLength()), newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull BashWord element) {
        return element.getTextContentRange();
    }
}
