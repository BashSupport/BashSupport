/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: StaticExpansionTest.java, Class: StaticExpansionTest
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

/**
 * Created by IntelliJ IDEA.
 * User: jansorg
 * Date: Nov 11, 2009
 * Time: 9:15:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class StaticExpansionTest {
    @Test
    public void testFindNext() throws Exception {
        Expansion e = new StaticExpansion("A");
        Assert.assertEquals("A", e.findNext(false));
        Assert.assertEquals("A", e.findNext(true));
        Assert.assertEquals("A", e.findNext(true));
    }
}
