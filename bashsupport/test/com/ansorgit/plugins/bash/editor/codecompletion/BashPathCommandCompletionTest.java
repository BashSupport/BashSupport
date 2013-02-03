/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPathCommandCompletionTest.java, Class: BashPathCommandCompletionTest
 * Last modified: 2013-02-03
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

package com.ansorgit.plugins.bash.editor.codecompletion;

import junit.framework.Assert;
import org.junit.Test;

/**
 */
public class BashPathCommandCompletionTest {
    @Test
    public void testFindUpperLimit() throws Exception {
        BashPathCommandCompletion completion = new BashPathCommandCompletion();
        completion.initComponent();

        Assert.assertEquals("abd", completion.findUpperLimit("abc"));
        Assert.assertEquals("abz", completion.findUpperLimit("aby"));
        Assert.assertEquals("ac", completion.findUpperLimit("abz"));
        Assert.assertEquals("ac", completion.findUpperLimit("ab"));
        Assert.assertEquals("az", completion.findUpperLimit("ay"));
        Assert.assertEquals("b", completion.findUpperLimit("az"));
        Assert.assertEquals("z", completion.findUpperLimit("z"));
    }
}
