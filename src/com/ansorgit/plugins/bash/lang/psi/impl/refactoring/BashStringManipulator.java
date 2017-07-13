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

import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashStringUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Element manipulator for String elenents.
 *
 * @author jansorg
 */
public class BashStringManipulator implements ElementManipulator<BashString> {
    @Override
    public BashString handleContentChange(@NotNull BashString element, @NotNull TextRange textRange, String contentForRange) throws IncorrectOperationException {
        TextRange elementContentRange = element.getTextContentRange();

        if (contentForRange.length() > 2 && textRange.getStartOffset() == 0 && textRange.getLength() == element.getTextLength()) {
            contentForRange = contentForRange.substring(1, contentForRange.length() - 1);
        }

        String escapedContent = BashStringUtils.escape(contentForRange, '"');
        String newContent = elementContentRange.replace(element.getText(), escapedContent);

        BashString replacement = BashPsiElementFactory.createString(element.getProject(), newContent);
        assert replacement != null;

        return BashPsiUtils.replaceElement(element, replacement);
    }

    @Override
    public BashString handleContentChange(@NotNull BashString element, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, TextRange.create(0, element.getTextLength()), newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull BashString element) {
        return element.getTextContentRange();
    }
}
