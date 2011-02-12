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

import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.openapi.diagnostic.Logger;
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

        new VariableNameCompletionProvider().addTo(this);
        new CommandNameCompletionProvider().addTo(this);
        new AbsolutePathCompletionProvider().addTo(this);
        new ShebangPathCompletionProvider().addTo(this);
        new DynamicPathCompletionProvider().addTo(this);
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        super.beforeCompletion(context);

        context.setDummyIdentifier("ZZZ");

        fixComposedWordEndOffset(context);
        //fixShebangEndOffset(context);
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
