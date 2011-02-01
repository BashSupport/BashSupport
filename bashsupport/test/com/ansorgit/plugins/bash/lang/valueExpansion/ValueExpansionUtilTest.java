/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ExpansionUtilTest.java, Class: ExpansionUtilTest
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

import com.intellij.openapi.util.text.StringUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * User: jansorg
 * Date: Nov 11, 2009
 * Time: 8:23:15 PM
 */
public class ValueExpansionUtilTest {
    @Test
    public void testInvalidPatternExpand() {
        Assert.assertFalse(ValueExpansionUtil.isValid("{a..}", false));
        Assert.assertFalse(ValueExpansionUtil.isValid("a..}", false));

        Assert.assertEquals("{a..}", ValueExpansionUtil.expand("{a..}", false));
    }

    @Test
    public void testExpand() throws Exception {
        Assert.assertEquals("a", ValueExpansionUtil.expand("a", false));
        Assert.assertEquals("a b c", ValueExpansionUtil.expand("{a,b,c}", false));

        Assert.assertEquals("a b c", ValueExpansionUtil.expand("{a..c}", false));
        Assert.assertEquals("a1 a2 b1 b2 c1 c2", ValueExpansionUtil.expand("{a,b,c}{1,2}", false));
        Assert.assertEquals("a1 a2 b1 b2 c1 c2", ValueExpansionUtil.expand("{a..c}{1..2}", false));

        Assert.assertEquals("1..2 a", ValueExpansionUtil.expand("{1..2,a}", false));
        Assert.assertEquals("1..2a", ValueExpansionUtil.expand("{1..2a}", false));
        Assert.assertEquals("ab..cd", ValueExpansionUtil.expand("{ab..cd}", false));

        Assert.assertEquals("aa ab ba bb", ValueExpansionUtil.expand("{a,b}{a,b}", false));
        Assert.assertEquals("aaa aab aba abb baa bab bba bbb", ValueExpansionUtil.expand("{a,b}{a,b}{a,b}", false));
    }

    @Test
    public void testSplit() {
        Assert.assertEquals(1, ValueExpansionUtil.split("a", false).size());
        Assert.assertEquals(2, ValueExpansionUtil.split("a{1}", false).size());
        Assert.assertEquals(2, ValueExpansionUtil.split("a{1,2}", false).size());
        Assert.assertEquals(3, ValueExpansionUtil.split("a{1,2}c", false).size());
        Assert.assertEquals(3, ValueExpansionUtil.split("{a,b,c}{a,b,c}{a,b,c}", false).size());

        List<Expansion> expansionList = ValueExpansionUtil.split("abc{1,2,3}xyz{hi}", false);
        Assert.assertEquals(4, expansionList.size());
    }

    @Test
    public void testEvaluatePattern() {
        Assert.assertEquals("a,b,c", StringUtil.join(ValueExpansionUtil.evaluateExpansionPattern("a,b,c", false), ","));
        Assert.assertEquals("a,b,c", StringUtil.join(ValueExpansionUtil.evaluateExpansionPattern("a..c", false), ","));
        Assert.assertEquals("1,2,3,4,5,6,7,8,9", StringUtil.join(ValueExpansionUtil.evaluateExpansionPattern("1..9", false), ","));
        Assert.assertEquals("2,3,4,5,6,7,8,9,10", StringUtil.join(ValueExpansionUtil.evaluateExpansionPattern("2..10", false), ","));
    }

    @Test
    public void testComplicated1() {
        String expected = "1baz 1bbz 1bcz 1bdz 1bez 1bfz 1bgz 1bhz 1biz 1bjz 1bkz 1blz 1bmz 1bnz 1boz 1bpz 1bqz 1brz 1bsz 1btz 1buz 1bvz 1bwz 1bxz 1byz 1bzz 2baz 2bbz 2bcz 2bdz 2bez 2bfz 2bgz 2bhz 2biz 2bjz 2bkz 2blz 2bmz 2bnz 2boz 2bpz 2bqz 2brz 2bsz 2btz 2buz 2bvz 2bwz 2bxz 2byz 2bzz 3baz 3bbz 3bcz 3bdz 3bez 3bfz 3bgz 3bhz 3biz 3bjz 3bkz 3blz 3bmz 3bnz 3boz 3bpz 3bqz 3brz 3bsz 3btz 3buz 3bvz 3bwz 3bxz 3byz 3bzz 4baz 4bbz 4bcz 4bdz 4bez 4bfz 4bgz 4bhz 4biz 4bjz 4bkz 4blz 4bmz 4bnz 4boz 4bpz 4bqz 4brz 4bsz 4btz 4buz 4bvz 4bwz 4bxz 4byz 4bzz 5baz 5bbz 5bcz 5bdz 5bez 5bfz 5bgz 5bhz 5biz 5bjz 5bkz 5blz 5bmz 5bnz 5boz 5bpz 5bqz 5brz 5bsz 5btz 5buz 5bvz 5bwz 5bxz 5byz 5bzz 6baz 6bbz 6bcz 6bdz 6bez 6bfz 6bgz 6bhz 6biz 6bjz 6bkz 6blz 6bmz 6bnz 6boz 6bpz 6bqz 6brz 6bsz 6btz 6buz 6bvz 6bwz 6bxz 6byz 6bzz 7baz 7bbz 7bcz 7bdz 7bez 7bfz 7bgz 7bhz 7biz 7bjz 7bkz 7blz 7bmz 7bnz 7boz 7bpz 7bqz 7brz 7bsz 7btz 7buz 7bvz 7bwz 7bxz 7byz 7bzz 8baz 8bbz 8bcz 8bdz 8bez 8bfz 8bgz 8bhz 8biz 8bjz 8bkz 8blz 8bmz 8bnz 8boz 8bpz 8bqz 8brz 8bsz 8btz 8buz 8bvz 8bwz 8bxz 8byz 8bzz 9baz 9bbz 9bcz 9bdz 9bez 9bfz 9bgz 9bhz 9biz 9bjz 9bkz 9blz 9bmz 9bnz 9boz 9bpz 9bqz 9brz 9bsz 9btz 9buz 9bvz 9bwz 9bxz 9byz 9bzz";
        Assert.assertEquals(expected, ValueExpansionUtil.expand("{1..9}b{a..z}z", false));
    }

    @Test
    public void testComplicated2() {
        String expected = "aaa aab aac aba abb abc aca acb acc baa bab bac bba bbb bbc bca bcb bcc caa cab cac cba cbb cbc cca ccb ccc";
        Assert.assertEquals(expected, ValueExpansionUtil.expand("{a,b,c}{a,b,c}{a,b,c}", false));
    }

    @Test
    public void testEnhancedForm() {
        Assert.assertEquals("1 3 5", ValueExpansionUtil.expand("{1..5..2}", true));
        Assert.assertEquals("1 3 5", ValueExpansionUtil.expand("{1..6..2}", true));
        Assert.assertEquals("1 6", ValueExpansionUtil.expand("{1..6..5}", true));
        Assert.assertEquals("a c e", ValueExpansionUtil.expand("{a..f..2}", true));
        Assert.assertEquals("001 002 003", ValueExpansionUtil.expand("{001..003}", true));
        Assert.assertEquals("01 02 03", ValueExpansionUtil.expand("{1..03}", true));
        Assert.assertEquals("03 02 01", ValueExpansionUtil.expand("{03..1}", true));
        Assert.assertEquals("03 02 01", ValueExpansionUtil.expand("{03..1..-1}", true));
    }
}
