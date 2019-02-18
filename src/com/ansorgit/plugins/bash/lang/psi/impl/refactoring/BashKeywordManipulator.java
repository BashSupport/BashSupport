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

import com.ansorgit.plugins.bash.lang.psi.api.BashKeyword;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Element manipulator implementation for HereDoc content elements.
 *
 * @author jansorg
 */
public class BashKeywordManipulator implements ElementManipulator<BashKeyword> {
    @Override
    public BashKeyword handleContentChange(@NotNull BashKeyword bashHereDoc, @NotNull TextRange textRange, String contentForRange) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    @Override
    public BashKeyword handleContentChange(@NotNull BashKeyword element, String newContent) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull BashKeyword element) {
        PsiElement keywordElement = element.keywordElement();
        if (keywordElement == null) {
            return TextRange.create(0, element.getTextLength());
        }

        return TextRange.create(0, keywordElement.getTextLength());
    }
}
