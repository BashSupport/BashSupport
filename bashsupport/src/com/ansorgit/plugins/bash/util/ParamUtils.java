/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ParamUtils.java, Class: ParamUtils
 * Last modified: 2009-12-04
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.util;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Date: 15.04.2009
 * Time: 22:02:02
 *
 * @author Joachim Ansorg
 */
public class ParamUtils {
    /**
     * Helper class to parse commands and their command line params.
     * <p/>
     * Bash most often has syntax descriptions like:
     * declare [-afFirtx] [-p] [name[=value] ...]
     *
     * @param combinedParams The combined list of params. e.g. "afFirtxp" for declare.
     * @param prefixes       The prefixes which are valid before each option, e.g. "+" and "-" for declare.
     * @return The set of string which represent the options, e.g. -a, +a, -f, +f, ...
     */
    public static Iterable<String> createParamList(String combinedParams, String... prefixes) {
        Set<String> result = Sets.newLinkedHashSet();

        final int length = combinedParams.length();
        for (int i = 0; i < length; ++i) {
            final String character = String.valueOf(combinedParams.charAt(i));
            for (final String prefix : prefixes) {
                result.add(prefix + character);
            }
        }

        return result;
    }
}
