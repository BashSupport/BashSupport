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

package com.ansorgit.plugins.bash.lang.lexer;

import org.junit.Assert;
import org.junit.Test;

public class HeredocLexingStateTest {
    @Test
    public void testInitialState() throws Exception {
        HeredocLexingState s = new HeredocLexingState();
        Assert.assertTrue(s.isEmpty());
        Assert.assertFalse(s.isExpectingEvaluatingHeredoc());

        Assert.assertFalse(s.isNextHeredocMarker("x"));
        Assert.assertFalse(s.isNextHeredocMarker("a"));
    }

    @Test
    public void testStateChange() throws Exception {
        HeredocLexingState s = new HeredocLexingState();

        s.pushHeredocMarker("a");
        Assert.assertTrue(s.isNextHeredocMarker("a"));
        Assert.assertFalse(s.isNextHeredocMarker("x"));

        s.popHeredocMarker("a");

        Assert.assertFalse(s.isNextHeredocMarker("a"));
        Assert.assertFalse(s.isNextHeredocMarker("x"));
    }

    @Test
    public void testEvaluatingHeredoc() throws Exception {
        HeredocLexingState s = new HeredocLexingState();

        s.pushHeredocMarker("\"a\"");
        Assert.assertFalse(s.isExpectingEvaluatingHeredoc());
        s.popHeredocMarker("a");

        s.pushHeredocMarker("a");
        Assert.assertTrue(s.isExpectingEvaluatingHeredoc());
        s.popHeredocMarker("a");
    }

    @Test(expected = java.lang.IllegalStateException.class)
    public void testInvalidStateChange() throws Exception {
        HeredocLexingState s = new HeredocLexingState();

        s.pushHeredocMarker("a");
        s.pushHeredocMarker("b");

        s.popHeredocMarker("x");
    }

    @Test(expected = java.lang.IllegalStateException.class)
    public void testInvalidStateWrongOrder() throws Exception {
        HeredocLexingState s = new HeredocLexingState();

        s.pushHeredocMarker("a");
        s.pushHeredocMarker("b");

        s.popHeredocMarker("b");
        s.popHeredocMarker("a");
    }
}