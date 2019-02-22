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

package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.OffsetMap;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import static com.intellij.codeInsight.completion.CompletionInitializationContext.IDENTIFIER_END_OFFSET;
import static com.intellij.codeInsight.completion.CompletionInitializationContext.START_OFFSET;

/**
 * Bash completion contributor.
 */
public class BashCompletionContributor extends CompletionContributor {
    private final static TokenSet endTokens = TokenSet.create(BashTokenTypes.STRING_END, BashTokenTypes.RIGHT_CURLY);
    private final static TokenSet wordTokens = TokenSet.create(BashTokenTypes.WORD);

    public BashCompletionContributor() {
        BashPathCompletionService completionService = BashPathCompletionService.getInstance();

        new VariableNameCompletionProvider().addTo(this);
        new CommandNameCompletionProvider(completionService).addTo(this);
        new AbsolutePathCompletionProvider().addTo(this);
        new ShebangPathCompletionProvider().addTo(this);
        new DynamicPathCompletionProvider().addTo(this);
        new BashKeywordCompletionProvider().addTo(this);
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        context.setDummyIdentifier("ZZZ");
    }

    @Override
    public void duringCompletion(@NotNull CompletionInitializationContext context) {
        fixComposedWordEndOffset(context);
    }

    /**
     * Fix the replace offset, for composed bash words use the full composed range
     *
     * @param context The current invocation context
     */
    protected void fixComposedWordEndOffset(CompletionInitializationContext context) {
        PsiElement element = context.getFile().findElementAt(context.getStartOffset());
        if (element == null) {
            return;
        }

        OffsetMap offsetMap = context.getOffsetMap();

        //if the completion is like "$<caret>" then element is the string end marker
        // in that case set the end before the end marker
        if (endTokens.contains(element.getNode().getElementType())) {
            offsetMap.addOffset(START_OFFSET, element.getTextOffset());
            offsetMap.addOffset(IDENTIFIER_END_OFFSET, element.getTextOffset());
            return;
        }

        if (wordTokens.contains(element.getNode().getElementType())) {
            if (fixReplacementOffsetInString(element, offsetMap)) {
                return;
            }
        }

        //try the parent if it's not already a BashWord
        if (!(element instanceof BashWord)) {
            element = element.getParent();
        }

        if (element instanceof BashWord) {
            offsetMap.addOffset(START_OFFSET, element.getTextOffset());

            // https://code.google.com/p/bashsupport/issues/detail?id=51
            // a completion at the end of a line with an open string is parsed as string until the first quote in the next line(s)
            // in the case of a newline character in the string the end offset is set to the end of the string to avoid aggressive string replacements

            if (!fixReplacementOffsetInString(element, offsetMap)) {
                offsetMap.addOffset(IDENTIFIER_END_OFFSET, element.getTextRange().getEndOffset());
            }

        }
    }

    /**
     * In a completion like "$abc<caret> def" the element is a word psi lead element, in this case to not expand the autocompletion after the whitespace character
     * https://code.google.com/p/bashsupport/issues/detail?id=51
     * a completion at the end of a line with an open string is parsed as string until the first quote in the next line(s)
     * in the case of a newline character in the string the end offset is set to the end of the string to avoid aggressive string replacements
     */
    private boolean fixReplacementOffsetInString(PsiElement element, OffsetMap offsetMap) {
        int endCharIndex = StringUtils.indexOfAny(element.getText(), new char[]{'\n', ' '});
        if (endCharIndex > 0) {
            offsetMap.addOffset(IDENTIFIER_END_OFFSET, element.getTextOffset() + endCharIndex);
            return true;
        }

        return false;
    }
}
