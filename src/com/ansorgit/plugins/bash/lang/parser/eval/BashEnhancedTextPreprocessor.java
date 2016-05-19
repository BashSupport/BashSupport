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

import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

/**
 * Handles a string which has just the escape codes
 * The Bash manual says about escape codes in strings:
 * <pre>
 *      Enclosing characters in double quotes preserves the literal value of all characters within the quotes, with the
 * exception of $, `, \, and, when history expansion is enabled, !.  The characters $ and ` retain  their  special
 * meaning  within double quotes.  The backslash retains its special meaning only when followed by one of the fol-
 * lowing characters: $, `, ", \, or <newline>.  A double quote may be quoted within double quotes by preceding it
 * with  a  backslash.  If enabled, history expansion will be performed unless an !  appearing in double quotes is
 * escaped using a backslash.  The backslash preceding the !  is not removed.
 *  </pre>
 * <br>
 * THis text preprocessor is used for $'' ASNI escaped strings. The valid escape values are documented at
 * http://wiki.bash-hackers.org/syntax/quoting .
 *
 * @author jansorg
 */
@SuppressWarnings("Duplicates")
class BashEnhancedTextPreprocessor implements TextPreprocessor {
    private int[] outSourceOffsets;
    private final TextRange contentRange;

    BashEnhancedTextPreprocessor(TextRange contentRange) {
        this.contentRange = contentRange;
    }

    @Override
    public boolean decode(String content, @NotNull StringBuilder outChars) {
        Ref<int[]> sourceOffsetsRef = new Ref<int[]>();
        boolean result = TextProcessorUtil.enhancedParseStringCharacters(content, outChars, sourceOffsetsRef);
        this.outSourceOffsets = sourceOffsetsRef.get();

        return result;
    }

    public int getOffsetInHost(int offsetInDecoded) {
        int result = offsetInDecoded >= 0 && offsetInDecoded < outSourceOffsets.length ? outSourceOffsets[offsetInDecoded] : -1;
        if (result == -1) {
            return -1;
        }

        return contentRange.getStartOffset() + (result <= contentRange.getLength() ? result : contentRange.getLength());
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
        return patchOriginal(originalText, null);
    }

    @Override
    public String patchOriginal(String originalText, String replacement) {
        return TextProcessorUtil.patchOriginal(originalText, outSourceOffsets, replacement);
    }
}