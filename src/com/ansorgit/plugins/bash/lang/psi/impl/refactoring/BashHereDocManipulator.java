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

import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
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
public class BashHereDocManipulator implements ElementManipulator<BashHereDoc> {
    @Override
    public BashHereDoc handleContentChange(@NotNull BashHereDoc bashHereDoc, @NotNull TextRange textRange, String contentForRange) throws IncorrectOperationException {
        String oldContent = bashHereDoc.getText();
        String newContent = textRange.replace(oldContent, contentForRange);

        PsiElement replacement = BashPsiElementFactory.createHeredocContent(bashHereDoc.getProject(), newContent);

        return BashPsiUtils.replaceElement(bashHereDoc, replacement);
    }

    @Override
    public BashHereDoc handleContentChange(@NotNull BashHereDoc element, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, TextRange.create(0, element.getTextLength()), newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull BashHereDoc element) {
        return TextRange.create(0, element.getTextLength());
    }
}
