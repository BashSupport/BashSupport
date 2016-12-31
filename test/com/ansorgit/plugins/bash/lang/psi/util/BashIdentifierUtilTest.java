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

/**
 */
public class BashIdentifierUtilTest {
    @Test
    public void testValidIdentifier() throws Exception {
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("a"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("abc"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("A"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("ABC123"));

        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("a_a"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("a_123"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("_123"));

        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("@"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("$"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("#"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("?"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("!"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("*"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("-"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("_"));

        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("0"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("1"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("2"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("3"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("4"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("5"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("6"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("7"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("8"));
        Assert.assertTrue(BashIdentifierUtil.isValidIdentifier("9"));

        Assert.assertFalse(BashIdentifierUtil.isValidIdentifier("123"));
        Assert.assertFalse(BashIdentifierUtil.isValidIdentifier("1a"));

        Assert.assertFalse(BashIdentifierUtil.isValidIdentifier("α"));
        Assert.assertFalse(BashIdentifierUtil.isValidIdentifier("α1"));
        Assert.assertFalse(BashIdentifierUtil.isValidIdentifier("разработка"));
    }
}