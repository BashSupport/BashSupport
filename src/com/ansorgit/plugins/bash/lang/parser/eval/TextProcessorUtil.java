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
import org.jetbrains.annotations.Nullable;

class TextProcessorUtil {
    static boolean hasNext(CharSequence chars, int index, CharSequence expected) {
        if (index + expected.length() >= chars.length()) {
            return false;
        }

        return chars.subSequence(index, index + expected.length()).equals(expected);
    }

    static void resetOffsets(int[] sourceOffsets) {
        for (int i = 0; i < sourceOffsets.length; i++) {
            sourceOffsets[i] = -1;
        }
    }

    public static String patchOriginal(String originalText, int[] outSourceOffsets, @Nullable String replacement) {
        StringBuilder result = new StringBuilder(originalText.length());

        int added = 0;
        for (int decoded = 1, original = outSourceOffsets[decoded];
             decoded < outSourceOffsets.length && original != -1 && original <= originalText.length();
             original = outSourceOffsets[decoded+1], decoded++) {

            for (int i = decoded + added; i < original; i++) {
                if (replacement == null) {
                    result.append(' ');
                    added++;
                } else {
                    result.append(replacement);
                    added += replacement.length();
                }
            }

            result.append(originalText.charAt(original - 1));

            if (decoded + 1 == outSourceOffsets.length) {
                break;
            }
        }

        return result.toString();
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
    static boolean parseStringCharacters(String chars, StringBuilder outChars, Ref<int[]> sourceOffsetsRef) {
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
        resetOffsets(sourceOffsets);

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

            if (c == '"' || /*c == '$' ||*/ c == '`' || c == '\\' || c == '!' || c == '\n' || (c == '$' && !hasNext(chars, index, "$$"))) {
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

    /**
     * Handles escape codes in evaluated string, e.g. the string in
     * <code>eval "echo \˜This is the value of \$x: $x\""</code>
     *
     * @param chars
     * @param outChars
     * @param sourceOffsetsRef
     * @return
     */
    static boolean enhancedParseStringCharacters(String chars, StringBuilder outChars, Ref<int[]> sourceOffsetsRef) {
        int[] sourceOffsets = new int[chars.length() + 1];
        sourceOffsetsRef.set(sourceOffsets);

        //init with -1
        resetOffsets(sourceOffsets);

        if (chars.indexOf('\\') < 0) {
            outChars.append(chars);
            for (int i = 0; i < sourceOffsets.length; i++) {
                sourceOffsets[i] = i;
            }
            return true;
        }

        int index = 0;
        while (index < chars.length()) {
            char c = chars.charAt(index++);

            sourceOffsets[outChars.length()] = index - 1;
            sourceOffsets[outChars.length() + 1] = index;

            if (c != '\\') {
                outChars.append(c);
                continue;
            }

            if (index == chars.length()) {
                return false;
            }

            c = chars.charAt(index++);
            switch (c) {
                //newline
                case 'n':
                    outChars.append('\n');
                    break;

                //return
                case 'r':
                    outChars.append('\r');
                    break;

                //tab
                case 't':
                    outChars.append('\t');
                    break;

                //vertical tab
                case 'v':
                    outChars.append(0x0B);
                    break;

                //backspace
                case 'b':
                    outChars.append('\b');
                    break;

                //alert
                case 'a':
                    outChars.append(0x07);
                    break;

                //escaped dollar
                //case '$':
                case '"':
                case '\'':
                case '\\':
                    outChars.append(c);
                    break;

                //octal
                case '0':
                    //fixme handle 1 to 3 possible octal numbers
                    if (index + 2 <= chars.length()) {
                        try {
                            int v = Integer.parseInt(chars.substring(index, index + 2), 8);
                            outChars.append((char) v);
                            index += 2;
                        } catch (Exception e) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                    break;

                //all other escape codes do not change the content
                default:
                    outChars.append('\\');
                    outChars.append(c);
                    break;
            }

            sourceOffsets[outChars.length()] = index;
        }
        return true;
    }
}
