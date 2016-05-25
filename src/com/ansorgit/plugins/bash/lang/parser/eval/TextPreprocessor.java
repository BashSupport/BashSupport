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

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a subset of {@link com.intellij.psi.LiteralTextEscaper} to be used by the {@link UnescapingPsiBuilder}.
 *
 * @author jansorg
 */
public interface TextPreprocessor {
    /**
     * Add decoded and unescaped characters from the host element to {@code outChars} buffer. If it's impossible to properly decode some chars
     * from the specified range (e.g. if the range starts or ends inside escaped sequence), decode the longest acceptable prefix of the range and return {@code false}
     *
     * @param content
     * @param outChars buffer for output chars. Use {@code append} methods only, <strong>it's forbidden to modify or remove existing characters</strong>
     * @return {@code true} if whole range was successfully decoded, {@code false} otherwise
     */
    boolean decode(String content, @NotNull StringBuilder outChars);

    /**
     * This method is called only after {@link #decode}, so it's possible to prepare necessary data in {@link #decode} and then use it here.
     *
     * @param offsetInDecoded offset in the parsed injected file
     * @return offset in the host PSI element, or -1 if offset is out of host range.
     * E.g. if some language fragment xyz was injected into string literal expression "xyz", then
     * getOffsetInHost(0)==1 (there is an 'x' at offset 0 in injected fragment,
     * and that 'x' occurs in "xyz" string literal at offset 1
     * since string literal expression "xyz" starts with double quote)
     * getOffsetInHost(1)==2
     * getOffsetInHost(2)==3
     * getOffsetInHost(3)==-1  (out of range)
     * <br>
     * Similarly, for some language fragment xyz being injected into xml text inside xml tag 'tag': <tag>xyz</tag>
     * getOffsetInHost(0)==0 (there is an 'x' at offset 0 in injected fragment,
     * and that 'x' occurs in xyz xml text at offset 0)
     * getOffsetInHost(1)==1
     * getOffsetInHost(2)==2
     * getOffsetInHost(3)==-1  (out of range)
     */
    int getOffsetInHost(int offsetInDecoded);

    TextRange getContentRange();

    boolean containsRange(int tokenStart, int tokenEnd);

    String patchOriginal(String originalText);

    String patchOriginal(String originalText, @Nullable String replacementChar);
}
