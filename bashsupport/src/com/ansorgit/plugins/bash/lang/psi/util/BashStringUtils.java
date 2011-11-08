/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashStringUtils.java, Class: BashStringUtils
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.psi.util;

/**
 * User: jansorg
 * Date: Dec 2, 2009
 * Time: 6:49:23 PM
 */
public class BashStringUtils {
    private BashStringUtils() {
    }

    public static int countPrefixChars(String data, char prefixChar) {
        int count = 0;
        for (int i = 0; i < data.length() && data.charAt(i) == prefixChar; ++i) {
            count++;
        }

        return count;
    }
}
