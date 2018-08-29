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
    public void testValidIdentifier() {
        Assert.assertTrue(isValidIdentifier("a"));
        Assert.assertTrue(isValidIdentifier("abc"));
        Assert.assertTrue(isValidIdentifier("A"));
        Assert.assertTrue(isValidIdentifier("ABC123"));

        Assert.assertTrue(isValidIdentifier("a_a"));
        Assert.assertTrue(isValidIdentifier("a_123"));
        Assert.assertTrue(isValidIdentifier("_123"));

        Assert.assertTrue(isValidIdentifier("@"));
        Assert.assertTrue(isValidIdentifier("$"));
        Assert.assertTrue(isValidIdentifier("#"));
        Assert.assertTrue(isValidIdentifier("?"));
        Assert.assertTrue(isValidIdentifier("!"));
        Assert.assertTrue(isValidIdentifier("*"));
        Assert.assertTrue(isValidIdentifier("-"));
        Assert.assertTrue(isValidIdentifier("_"));

        Assert.assertTrue(isValidIdentifier("0"));
        Assert.assertTrue(isValidIdentifier("1"));
        Assert.assertTrue(isValidIdentifier("2"));
        Assert.assertTrue(isValidIdentifier("3"));
        Assert.assertTrue(isValidIdentifier("4"));
        Assert.assertTrue(isValidIdentifier("5"));
        Assert.assertTrue(isValidIdentifier("6"));
        Assert.assertTrue(isValidIdentifier("7"));
        Assert.assertTrue(isValidIdentifier("8"));
        Assert.assertTrue(isValidIdentifier("9"));
        Assert.assertTrue(isValidIdentifier("11"));
        Assert.assertTrue(isValidIdentifier("100"));
        Assert.assertTrue(isValidIdentifier("110"));

        Assert.assertFalse(isValidIdentifier("1a"));
        Assert.assertFalse(isValidIdentifier("a$a"));
//        Assert.assertFalse(isValidIdentifier("a-a"));
        Assert.assertFalse(isValidIdentifier("@a"));

        Assert.assertFalse(isValidIdentifier("α"));
        Assert.assertFalse(isValidIdentifier("α1"));
        Assert.assertFalse(isValidIdentifier("разработка"));
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
