/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashStringUtilsTest.java, Class: BashStringUtilsTest
 * Last modified: 2010-01-11
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

package com.ansorgit.plugins.bash.lang.psi.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jansorg
 */
public class BashStringUtilsTest {
    @Test
    public void testCountPrefixChars() throws Exception {
        Assert.assertEquals(0, BashStringUtils.countPrefixChars("1", '0'));
        Assert.assertEquals(1, BashStringUtils.countPrefixChars("01", '0'));
        Assert.assertEquals(3, BashStringUtils.countPrefixChars("0001", '0'));
        Assert.assertEquals(10, BashStringUtils.countPrefixChars("00000000001", '0'));
    }

    @Test
    public void testEscape() throws Exception {
        Assert.assertEquals("abc", BashStringUtils.escape("abc", '$'));
        Assert.assertEquals("\\'", BashStringUtils.escape("'", '\''));
        Assert.assertEquals("\\'\\'", BashStringUtils.escape("''", '\''));
        Assert.assertEquals("\\'\\'\\'\\'", BashStringUtils.escape("''''", '\''));
        Assert.assertEquals("abc\\$", BashStringUtils.escape("abc$", '$'));
        Assert.assertEquals("\\'abc\\'", BashStringUtils.escape("'abc'", '\''));
        Assert.assertEquals("\\'abc\\'", BashStringUtils.escape("\\'abc'", '\''));

        // \\" ' -> \\" \'
        Assert.assertEquals("a\\\"\\'", BashStringUtils.escape("a\\\"'", '\''));

        // \\\" ' ->  \\\" '
        Assert.assertEquals("\\\\\\\" \\'", BashStringUtils.escape("\\\\\\\" '", '\''));
        // \\\" ' -> \\\\\\" '
        Assert.assertEquals("\\\\\\\\\\\\\" '", BashStringUtils.escape("\\\\\\\" '", '\\'));
        // \a ->  \\a
        Assert.assertEquals("\\\\a", BashStringUtils.escape("\\a", '\\'));

        // \\ ->  \\\\
        Assert.assertEquals("\\\\\\\\", BashStringUtils.escape("\\\\", '\\'));

        //already escaped shouldn't be escaped again
        Assert.assertEquals("\\$", BashStringUtils.escape("\\$", '$'));

        Assert.assertEquals("\\$", BashStringUtils.escape("\\$", '\\', new char[]{'$'}));
    }
}
