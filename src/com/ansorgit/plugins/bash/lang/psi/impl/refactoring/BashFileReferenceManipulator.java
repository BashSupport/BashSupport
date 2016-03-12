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

import com.ansorgit.plugins.bash.lang.psi.api.BashCharSequence;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Element manipulator for Bash file references.
 *
 * @author jansorg
 */
public class BashFileReferenceManipulator implements ElementManipulator {
    @Override
    public PsiElement handleContentChange(@NotNull PsiElement element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        PsiElement firstChild = element.getFirstChild();

        String name;
        if (firstChild instanceof BashCharSequence) {
            name = ((BashCharSequence) firstChild).createEquallyWrappedString(newContent);
        } else {
            name = newContent;
        }

        return BashPsiUtils.replaceElement(element, BashPsiElementFactory.createFileReference(element.getProject(), name));
    }

    @Override
    public PsiElement handleContentChange(@NotNull final PsiElement element, final String newContent) throws IncorrectOperationException {
        return handleContentChange(element, TextRange.create(0, element.getTextLength()), newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull PsiElement element) {
        PsiElement firstChild = element.getFirstChild();
        if (firstChild instanceof BashCharSequence) {
            return ((BashCharSequence) firstChild).getTextContentRange().shiftRight(firstChild.getStartOffsetInParent());
        }

        return TextRange.from(0, element.getTextLength());
    }
}
