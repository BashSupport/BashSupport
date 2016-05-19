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
package com.ansorgit.plugins.bash.lang.parser.eval;

import com.intellij.openapi.util.ProperTextRange;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

public class BashIdentityTextPreprocessor implements TextPreprocessor {
    private final TextRange contentRange;

    public BashIdentityTextPreprocessor(TextRange contentRange) {
        ProperTextRange.assertProperRange(contentRange);
        this.contentRange = contentRange;
    }

    @Override
    public boolean decode(String content, @NotNull StringBuilder outChars) {
        outChars.append(content);
        return true;
    }

    public int getOffsetInHost(int offsetInDecoded) {
        return offsetInDecoded + contentRange.getStartOffset();
    }

    @Override
    public TextRange getContentRange() {
        return contentRange;
    }

    @Override
    public boolean containsRange(int tokenStart, int tokenEnd) {
        return getContentRange().containsRange(tokenStart, tokenEnd);
    }

    @Override
    public String patchOriginal(String originalText) {
        return originalText;
    }

    @Override
    public String patchOriginal(String originalText, String replacement) {
        return originalText;
    }
}