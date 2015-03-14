/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: IteratingExpansionTest.java, Class: IteratingExpansionTest
 * Last modified: 2010-01-27
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

package com.ansorgit.plugins.bash.lang.valueExpansion;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * User: jansorg
 * Date: Nov 11, 2009
 * Time: 9:12:54 PM
 */
public class IteratingExpansionTest {
    @Test
    public void testFindNextSimple() throws Exception {
        List<String> values = new LinkedList<String>();
        values.add("a");

        Expansion e = new IteratingExpansion(values);
        Assert.assertEquals("a", e.findNext(false));
        Assert.assertEquals("a", e.findNext(false));
        Assert.assertEquals("a", e.findNext(false));
    }

    @Test
    public void testFindNextComplex() throws Exception {
        List<String> values = new LinkedList<String>();
        values.add("a");
        values.add("b");
        values.add("c");

        Expansion e = new IteratingExpansion(values);
        Assert.assertEquals("a", e.findNext(false));
        Assert.assertEquals("a", e.findNext(false));
        Assert.assertFalse(e.isFlipped());

        Assert.assertEquals("b", e.findNext(true));
        Assert.assertEquals("b", e.findNext(false));
        Assert.assertFalse(e.isFlipped());

        Assert.assertEquals("c", e.findNext(true));
        Assert.assertFalse(e.isFlipped());

        Assert.assertEquals("a", e.findNext(true));
        Assert.assertTrue(e.isFlipped());

        Assert.assertEquals("b", e.findNext(true));
        Assert.assertEquals("c", e.findNext(true));

        Assert.assertEquals("a", e.findNext(true));
        Assert.assertTrue(e.isFlipped());
    }
}
