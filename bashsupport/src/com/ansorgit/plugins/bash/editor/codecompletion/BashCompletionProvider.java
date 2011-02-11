/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashCompletionProvider.java, Class: BashCompletionProvider
 * Last modified: 2010-03-28
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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Abstract base class for completion providers in Bash files.
 * <p/>
 * User: jansorg
 * Date: Dec 4, 2009
 * Time: 12:27:29 AM
 */
abstract class BashCompletionProvider extends CompletionProvider<CompletionParameters> {
    public BashCompletionProvider() {
    }

    abstract void addTo(CompletionContributor contributor);

    protected Predicate<File> createFileFilter() {
        return Predicates.alwaysTrue();
    }

    @Override
    protected final void addCompletions(@NotNull CompletionParameters parameters,
                                        ProcessingContext context,
                                        @NotNull CompletionResultSet resultWithoutPrefix) {

        addBashCompletions(findCurrentText(parameters, parameters.getPosition()), parameters, context, resultWithoutPrefix);
    }

    protected String findOriginalText(PsiElement element) {
        return element.getText();
    }

    protected String findCurrentText(CompletionParameters parameters, PsiElement element) {
        String originalText = findOriginalText(element);
        int elementOffset = parameters.getOffset() - element.getTextOffset();

        return (elementOffset >= 0) && (elementOffset < originalText.length())
                ? originalText.substring(0, elementOffset)
                : originalText;
    }

    protected abstract void addBashCompletions(String currentText, CompletionParameters parameters, ProcessingContext context, CompletionResultSet resultWithoutPrefix);
}
