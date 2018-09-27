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

import org.junit.Assert;
import org.junit.Test;

import static com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil.*;

/**
 */
public class BashIdentifierUtilTest {
    @Test
    public void testValidVariableName() {
        Assert.assertTrue(isValidVariableName("a"));
        Assert.assertTrue(isValidVariableName("abc"));
        Assert.assertTrue(isValidVariableName("A"));
        Assert.assertTrue(isValidVariableName("ABC123"));

        Assert.assertTrue(isValidVariableName("a_a"));
        Assert.assertTrue(isValidVariableName("a_123"));
        Assert.assertTrue(isValidVariableName("_123"));

        Assert.assertTrue(isValidVariableName("@"));
        Assert.assertTrue(isValidVariableName("$"));
        Assert.assertTrue(isValidVariableName("#"));
        Assert.assertTrue(isValidVariableName("?"));
        Assert.assertTrue(isValidVariableName("!"));
        Assert.assertTrue(isValidVariableName("*"));
        Assert.assertTrue(isValidVariableName("-"));
        Assert.assertTrue(isValidVariableName("_"));

        Assert.assertTrue(isValidVariableName("0"));
        Assert.assertTrue(isValidVariableName("1"));
        Assert.assertTrue(isValidVariableName("2"));
        Assert.assertTrue(isValidVariableName("3"));
        Assert.assertTrue(isValidVariableName("4"));
        Assert.assertTrue(isValidVariableName("5"));
        Assert.assertTrue(isValidVariableName("6"));
        Assert.assertTrue(isValidVariableName("7"));
        Assert.assertTrue(isValidVariableName("8"));
        Assert.assertTrue(isValidVariableName("9"));
        Assert.assertTrue(isValidVariableName("11"));
        Assert.assertTrue(isValidVariableName("100"));
        Assert.assertTrue(isValidVariableName("110"));

        Assert.assertFalse(isValidVariableName("1a"));
        Assert.assertFalse(isValidVariableName("a$a"));
        Assert.assertFalse(isValidVariableName("@a"));
        Assert.assertFalse(isValidVariableName("a-a"));

        Assert.assertFalse(isValidVariableName("α"));
        Assert.assertFalse(isValidVariableName("α1"));
        Assert.assertFalse(isValidVariableName("разработка"));
    }

    @Test
    public void testValidFunctionName() {
        Assert.assertTrue(isValidFunctionName("_"));
        Assert.assertTrue(isValidFunctionName("_a"));
        Assert.assertTrue(isValidFunctionName("-"));
        Assert.assertTrue(isValidFunctionName("-a"));
        Assert.assertTrue(isValidFunctionName("a-a"));
        Assert.assertTrue(isValidFunctionName("a-a-c_def"));
        Assert.assertTrue(isValidFunctionName("1-a"));
        Assert.assertTrue(isValidFunctionName("1_a"));

        Assert.assertFalse(isValidFunctionName("1"));
        Assert.assertFalse(isValidFunctionName("123"));
    }

    @Test
    public void testValidNewVariableName() {
        Assert.assertTrue(isValidNewVariableName("abcde_xyz0123456789"));

        Assert.assertFalse(isValidNewVariableName(""));
        Assert.assertFalse(isValidNewVariableName("0"));
        Assert.assertFalse(isValidNewVariableName("0a"));
        Assert.assertFalse(isValidNewVariableName("$"));
        Assert.assertFalse(isValidNewVariableName("a$a"));
        Assert.assertFalse(isValidNewVariableName("разработка"));
    }
}
