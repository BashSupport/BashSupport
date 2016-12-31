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

/**
 * @author jansorg
 */
public final class BashIdentifierUtil {
    private BashIdentifierUtil() {
    }

    public static boolean isValidNewVariableName(String text) {
        return isValidIdentifier(text); //fixme should be enhanced (no time atm)
    }

    public static boolean isValidIdentifier(CharSequence text) {
        if (text == null || text.length() == 0) {
            return false;
        }

        char first = text.charAt(0);

        if (first >= '0' && first <= '9') {
            //builtin $1 to $9 are valid, all other variables which start with a digit are not
            return text.length() == 1;
        }

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < '0' || c > '9') && c != '_' && c != '@' && c != '$' && c != '#' && c != '?' && c != '!' && c != '*' && c != '-') {
                return false;
            }
        }

        return true;
    }
}
