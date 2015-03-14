/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: SimpleExpressionsImplTest.java, Class: SimpleExpressionsImplTest
 * Last modified: 2010-05-26
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

package com.ansorgit.plugins.bash.lang.psi.impl.arithmetic;

import junit.framework.Assert;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 26.05.2010
 * Time: 21:49:24
 */
public class SimpleExpressionsImplTest {
    @Test
    public void testDecimalLiteral() {
        Assert.assertEquals(0, SimpleExpressionsImpl.baseLiteralValue(10, "0"));
        Assert.assertEquals(123, SimpleExpressionsImpl.baseLiteralValue(10, "123"));
        Assert.assertEquals(123456, SimpleExpressionsImpl.baseLiteralValue(10, "123456"));
    }

    @Test
    public void testOctalLiteral() {
        Assert.assertEquals(0, SimpleExpressionsImpl.baseLiteralValue(8, "0"));
        Assert.assertEquals(1, SimpleExpressionsImpl.baseLiteralValue(8, "1"));
        Assert.assertEquals(7, SimpleExpressionsImpl.baseLiteralValue(8, "7"));
        Assert.assertEquals(342391, SimpleExpressionsImpl.baseLiteralValue(8, "1234567"));
    }

    @Test
    public void testHexLiteral() {
        Assert.assertEquals(0, SimpleExpressionsImpl.baseLiteralValue(16, "0"));
        Assert.assertEquals(1, SimpleExpressionsImpl.baseLiteralValue(16, "1"));
        Assert.assertEquals(7, SimpleExpressionsImpl.baseLiteralValue(16, "7"));
        Assert.assertEquals(10, SimpleExpressionsImpl.baseLiteralValue(16, "a"));
        Assert.assertEquals(10, SimpleExpressionsImpl.baseLiteralValue(16, "A"));
        Assert.assertEquals(15, SimpleExpressionsImpl.baseLiteralValue(16, "f"));
        Assert.assertEquals(15, SimpleExpressionsImpl.baseLiteralValue(16, "F"));
    }

    @Test
    public void testBase36() throws Exception {
        Assert.assertEquals(10, SimpleExpressionsImpl.baseLiteralValue(36, "a"));
        Assert.assertEquals(10, SimpleExpressionsImpl.baseLiteralValue(36, "A"));

        Assert.assertEquals(35, SimpleExpressionsImpl.baseLiteralValue(36, "z"));
        Assert.assertEquals(35, SimpleExpressionsImpl.baseLiteralValue(36, "Z"));
    }

    @Test
    public void testBase37() throws Exception {
        Assert.assertEquals(10, SimpleExpressionsImpl.baseLiteralValue(37, "a"));
        Assert.assertEquals(36, SimpleExpressionsImpl.baseLiteralValue(37, "A"));

        Assert.assertEquals(35, SimpleExpressionsImpl.baseLiteralValue(37, "z"));
    }

    @Test
    public void testBase64() {
        Assert.assertEquals(0, SimpleExpressionsImpl.baseLiteralValue(64, "0"));
        Assert.assertEquals(1, SimpleExpressionsImpl.baseLiteralValue(64, "1"));
        Assert.assertEquals(7, SimpleExpressionsImpl.baseLiteralValue(64, "7"));
        Assert.assertEquals(62, SimpleExpressionsImpl.baseLiteralValue(64, "@"));
        Assert.assertEquals(63, SimpleExpressionsImpl.baseLiteralValue(64, "_"));
        Assert.assertEquals(4227, SimpleExpressionsImpl.baseLiteralValue(64, "123"));
        Assert.assertEquals(1073741823, SimpleExpressionsImpl.baseLiteralValue(64, "_____"));
    }
}
