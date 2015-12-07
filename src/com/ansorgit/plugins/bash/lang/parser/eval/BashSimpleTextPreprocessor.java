/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
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
 *
 * @author jansorg
 */
@SuppressWarnings("Duplicates")
public class BashSimpleTextPreprocessor implements TextPreprocessor {
    private int[] outSourceOffsets;
    private TextRange contentRange;

    public BashSimpleTextPreprocessor(TextRange contentRange) {
        this.contentRange = contentRange;
    }

    /**
     * Handles escape codes in evaluated string, e.g. the string in
     * <code>eval "echo \˜This is the value of \$x: $x\""</code>
     *
     * @param chars
     * @param outChars
     * @param sourceOffsetsRef
     * @return
     */
    private static boolean parseStringCharacters(String chars, StringBuilder outChars, Ref<int[]> sourceOffsetsRef) {
        int[] sourceOffsets = new int[chars.length() + 1];
        sourceOffsetsRef.set(sourceOffsets);

        //if there is no escape code in the text create a simple offset mapping (source position is target position)
        if (chars.indexOf('\\') < 0) {
            outChars.append(chars);
            for (int i = 0; i < sourceOffsets.length; i++) {
                sourceOffsets[i] = i;
            }
            return true;
        }

        //init with -1
        for (int i = 0; i < sourceOffsets.length; i++) {
            sourceOffsets[i] = -1;
        }

        int index = 0;
        while (index < chars.length()) {
            char c = chars.charAt(index);
            index++;

            sourceOffsets[outChars.length()] = index - 1;
            sourceOffsets[outChars.length() + 1] = index;

            if (c != '\\') {
                //no escape code
                outChars.append(c);
                continue;
            }

            if (index == chars.length()) {
                //backslash is the last character, append it as-is
                outChars.append(c);
                return true;
            }

            //handle the escape code character
            c = chars.charAt(index);

            if (c == '"' || /*c == '$' ||*/ c == '`' || c == '\\' || c == '!' || c == '\n' || hasNext(chars, index, "$(")) {
                if (c != '\n') {
                    //the current character is a valid escape code and will be replaced with it's escaped value
                    outChars.append(c);
                }
            } else {
                //all other characters retain their original meaning, i.e. \a is \a without escape code interpretation
                outChars.append('\\');
                outChars.append(c);
            }

            index++;
            sourceOffsets[outChars.length()] = index;
        }
        return true;
    }

    private static boolean hasNext(CharSequence chars, int index, CharSequence expected) {
        if (index + expected.length() >= chars.length()) {
            return false;
        }

        return chars.subSequence(index, index + expected.length()).equals(expected);
    }

    @Override
    public boolean decode(String content, @NotNull StringBuilder outChars) {
        Ref<int[]> sourceOffsetsRef = new Ref<int[]>();

        boolean result = parseStringCharacters(content, outChars, sourceOffsetsRef);

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
}