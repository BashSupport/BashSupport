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

package com.ansorgit.plugins.bash.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.EmptyStackException;

/**
 */
public class IntStackTest {
    @Test
    public void testStack() throws Exception {
        IntStack stack = new IntStack(10);

        Assert.assertTrue(stack.empty());

        stack.push(123);
        Assert.assertFalse(stack.empty());
        Assert.assertTrue(stack.contains(123));
        Assert.assertFalse(stack.contains(456));

        stack.push(456);
        Assert.assertFalse(stack.empty());
        Assert.assertTrue(stack.contains(123));
        Assert.assertTrue(stack.contains(456));
        Assert.assertEquals(456, stack.peek());

        stack.pop();
        Assert.assertFalse(stack.empty());
        Assert.assertTrue(stack.contains(123));
        Assert.assertFalse(stack.contains(456));
        Assert.assertEquals(123, stack.peek());

        stack.pop();
        Assert.assertTrue(stack.empty());
        Assert.assertFalse(stack.contains(123));
        Assert.assertFalse(stack.contains(456));
    }

    @Test(expected = EmptyStackException.class)
    public void testEmptyPop() throws Exception {
        new IntStack(10).pop();
    }

    @Test(expected = EmptyStackException.class)
    public void testEmptyPeek() throws Exception {
        new IntStack(10).peek();
    }
}