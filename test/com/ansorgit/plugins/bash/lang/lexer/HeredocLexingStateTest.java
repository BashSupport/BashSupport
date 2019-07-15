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

        Assert.assertFalse(s.isNextMarker("x"));
        Assert.assertFalse(s.isNextMarker("a"));
    }

    @Test(expected = IllegalStateException.class)
    public void testInitialStateAssertion() throws Exception {
        HeredocLexingState s = new HeredocLexingState();
        Assert.assertFalse(s.isExpectingEvaluatingHeredoc());
    }

    @Test
    public void testStateChange() throws Exception {
        HeredocLexingState s = new HeredocLexingState();

        s.pushMarker(0, "a", false);
        Assert.assertTrue(s.isNextMarker("a"));
        Assert.assertFalse(s.isNextMarker("x"));

        s.popMarker("a");

        Assert.assertFalse(s.isNextMarker("a"));
        Assert.assertFalse(s.isNextMarker("x"));
    }

    @Test
    public void testEvaluatingHeredoc() throws Exception {
        HeredocLexingState s = new HeredocLexingState();

        s.pushMarker(0, "\"a\"", false);
        Assert.assertFalse(s.isExpectingEvaluatingHeredoc());
        s.popMarker("a");

        s.pushMarker(0, "a", false);
        Assert.assertTrue(s.isExpectingEvaluatingHeredoc());
        s.popMarker("a");
    }

    @Test(expected = java.lang.IllegalStateException.class)
    public void testInvalidStateChange() throws Exception {
        HeredocLexingState s = new HeredocLexingState();

        s.pushMarker(0, "a", false);
        s.pushMarker(0, "b", false);

        s.popMarker("x");
    }

    @Test(expected = java.lang.IllegalStateException.class)
    public void testInvalidStateWrongOrder() throws Exception {
        HeredocLexingState s = new HeredocLexingState();

        s.pushMarker(0, "a", false);
        s.pushMarker(0, "b", false);

        s.popMarker("b");
        s.popMarker("a");
    }
}