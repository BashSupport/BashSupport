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

import static org.junit.Assert.*;

/**
 * @author jansorg
 */
public class BashCommandUtilTest {
    @Test
    public void simpleArg() {
        Assert.assertTrue(BashCommandUtil.isParameterDefined("-a", "-ra"));
        Assert.assertTrue(BashCommandUtil.isParameterDefined("-a", "-rast"));
        Assert.assertTrue(BashCommandUtil.isParameterDefined("-h", "-help"));

        Assert.assertFalse(BashCommandUtil.isParameterDefined("-h", "--help"));
    }

    @Test
    public void completArg() {
        Assert.assertTrue(BashCommandUtil.isParameterDefined("--a", "--a"));
        Assert.assertTrue(BashCommandUtil.isParameterDefined("--help", "--help"));

        Assert.assertFalse(BashCommandUtil.isParameterDefined("--help", "--helper"));
    }
}