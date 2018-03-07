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
public final class BashCommandUtil {
    private BashCommandUtil() {
    }

    /**
     * @param parameterName The parameter to look for, e.g. "-a"
     * @param argumentValue The actual value passed on the command line, e.g."-ra"
     * @return true if parameterName is defined in argumentValue
     */
    public static boolean isParameterDefined(String parameterName, String argumentValue) {
        if (parameterName.equals(argumentValue)) {
            return true;
        }

        //interpret -X as a one-letter argument, e.g. "-a" and "-r" are defined in "-ra"
        if (isSimpleArg(parameterName) && isSimpleArg(argumentValue) && parameterName.length() == 2) {
            return argumentValue.contains(parameterName.substring(1));
        }

        return false;
    }

    private static boolean isSimpleArg(String parameterName) {
        return parameterName.startsWith("-") && !parameterName.startsWith("--");
    }
}
