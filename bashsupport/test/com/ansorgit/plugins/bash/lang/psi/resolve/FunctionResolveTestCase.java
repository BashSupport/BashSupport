/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: FunctionResolveTestCase.java, Class: FunctionResolveTestCase
 * Last modified: 2010-06-30
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

package com.ansorgit.plugins.bash.lang.psi.resolve;

import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import junit.framework.Assert;

/**
 * User: jansorg
 * Date: 15.06.2010
 * Time: 19:14:20
 */
public class FunctionResolveTestCase extends AbstractResolveTest {
    public void testBasicFunctionResolve() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashFunctionDef);
    }

    public void testBasicFunctionResolveSelf() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashFunctionDef);
    }

    public void testBasicFunctionResolveInner() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashFunctionDef);
    }

    protected String getTestDataPath() {
        return getBasePath() + "/psi/resolve/function/";
    }
}
