/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: LanguageBuiltinsTest.java, Class: LanguageBuiltinsTest
 * Last modified: 2010-04-21
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

package com.ansorgit.plugins.bash.lang;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 21.04.2010
 * Time: 22:32:32
 */
public class LanguageBuiltinsTest {
    @Test
    public void testBuiltinReadonly() {
        for (String name : LanguageBuiltins.readonlyShellVars) {
            Assert.assertTrue("Not found: " + name, LanguageBuiltins.bashShellVars.contains(name));
        }
    }
}
