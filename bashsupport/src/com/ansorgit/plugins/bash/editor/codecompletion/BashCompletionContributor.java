/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashCompletionContributor.java, Class: BashCompletionContributor
 * Last modified: 2010-05-13
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

package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Bash completion contributor.
 * <p/>
 * User: jansorg
 * Date: Dec 3, 2009
 * Time: 2:35:32 PM
 */
public class BashCompletionContributor extends CompletionContributor {
    private static final Logger log = Logger.getInstance("BashCompletionContributor");

    public BashCompletionContributor() {
        log.info("Created bash completion contributor");

        extend(CompletionType.BASIC, StandardPatterns.instanceOf(PsiElement.class), new VariableNameCompletionProvider());

        extend(CompletionType.BASIC, StandardPatterns.instanceOf(PsiElement.class), new AbsolutePathCompletionProvider());
        extend(CompletionType.BASIC, StandardPatterns.instanceOf(PsiElement.class), new DynamicPathCompletionProvider());
        extend(CompletionType.BASIC, StandardPatterns.instanceOf(PsiElement.class), new ShebangPathCompletionProvider());
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        super.beforeCompletion(context);

        fixComposedWordEndOffset(context);
        fixShebangEndOffset(context);
    }

    /**
     * If we're working on a BashShebang we fix the end offset of the replace action to include the full command
     *
     * @param context The current invocation context
     */
    private void fixShebangEndOffset(CompletionInitializationContext context) {
        PsiElement element = context.getFile().findElementAt(context.getStartOffset());
        if ((element == null)) {
            return;
        }

        if (!(element instanceof BashShebang)) {
            element = element.getParent();
        }

        if (element instanceof BashShebang) {
            BashShebang shebang = (BashShebang) element;
            if (shebang.shellCommand() != null) {
                setNewEndOffset(context, element.getTextOffset() + shebang.commandRange().getEndOffset());
            }
        }
    }

    /**
     * Fix the replace offset, for composed bash words use the full composed range
     *
     * @param context The current invocation context
     */
    private void fixComposedWordEndOffset(CompletionInitializationContext context) {
        PsiElement element = context.getFile().findElementAt(context.getStartOffset());
        if (element == null) {
            return;
        }

        //try the parent if it's not already a BashWord
        if (!(element instanceof BashWord)) {
            element = element.getParent();
        }

        if (element instanceof BashWord) {
            int endOffset = element.getTextOffset() + element.getTextLength();
            setNewEndOffset(context, endOffset);
        }
    }

    private void setNewEndOffset(CompletionInitializationContext context, int endOffset) {
        context.getOffsetMap().addOffset(CompletionInitializationContext.IDENTIFIER_END_OFFSET, endOffset);
    }
}
