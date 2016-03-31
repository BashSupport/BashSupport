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

package com.ansorgit.plugins.bash.lang.psi.util;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author jansorg
 */
public final class BashStringUtils {
    private static final char[] EMPTY = new char[0];

    private BashStringUtils() {
    }

    public static int countPrefixChars(String data, char prefixChar) {
        int count = 0;
        for (int i = 0; i < data.length() && data.charAt(i) == prefixChar; ++i) {
            count++;
        }

        return count;
    }

    public static String escape(CharSequence content, char escapedChar) {
        return escape(content, escapedChar, EMPTY);
    }


    /**
     * Escaped occurrences of escapedChar. If the found character is already escaped then it is not escaped again.
     *
     * @param content
     * @param escapedChar
     * @return New content with escaped occurrences of escapedChar
     */
    public static String escape(CharSequence content, char escapedChar, char[] ignoredIfFollowedBy) {
        StringBuilder builder = new StringBuilder();

        char last = 0;
        for (int i = 0; i < content.length(); ++i) {
            char current = content.charAt(i);

            if (current == escapedChar && last != '\\') {
                if (i == content.length() - 1 || !ArrayUtils.contains(ignoredIfFollowedBy, content.charAt(i + 1))) {
                builder.append('\\');
            }
            }

            builder.append(current);

            last = (escapedChar == '\\' && current == '\\') ? 0 : current;
        }

        return builder.toString();
    }
}
