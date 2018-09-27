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

import java.util.regex.Pattern;

/**
 * @author jansorg
 */
public final class BashIdentifierUtil {
    private static final Pattern newVariablePattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
    private static final Pattern singleIdentifier = Pattern.compile("[@$?!*#-]");
    private static final Pattern anyIdentifier = Pattern.compile("[0-9]+|([a-zA-Z_][a-zA-Z0-9_]*)");
    private static final Pattern functionIdentifier = Pattern.compile("[a-zA-Z0-9_-]+");
    private static final Pattern heredocMarker = Pattern.compile("[^ ]+");

    private static final Pattern number = Pattern.compile("[0-9]+");

    private BashIdentifierUtil() {
    }

    public static boolean isValidNewVariableName(String text) {
        return text != null && newVariablePattern.matcher(text).matches();
    }

    public static boolean isValidFunctionName(String text) {
        return text != null && functionIdentifier.matcher(text).matches() && !number.matcher(text).matches();
    }

    public static boolean isValidVariableName(CharSequence text) {
        return text != null && (singleIdentifier.matcher(text).matches() || anyIdentifier.matcher(text).matches());
    }

    public static boolean isValidHeredocIdentifier(CharSequence text) {
        return text != null && heredocMarker.matcher(text).matches();
    }
}
