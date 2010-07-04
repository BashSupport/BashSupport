/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: VarDefResolveTestCase.java, Class: VarDefResolveTestCase
 * Last modified: 2010-07-01
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

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.psi.PsiReference;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;

/**
 * User: jansorg
 * Date: 15.06.2010
 * Time: 19:14:20
 */
public class VarDefResolveTestCase extends AbstractResolveTest {
    private void assertIsValidVarDef() throws Exception {
        PsiReference ref = configure();
        Assert.assertTrue(ref.resolve() instanceof BashVarDef);
        Assert.assertFalse(ref.equals(ref.resolve()));
        Assert.assertTrue(ref.isReferenceTo(ref.resolve()));
    }

    public void testGlobalVarDef() throws Exception {
        assertIsValidVarDef();
    }

    public void testGlobalVarDefWithLocal() throws Exception {
        assertIsValidVarDef();
    }

    public void testVarDefFromOuterFunction() throws Exception {
        assertIsValidVarDef();
    }

    public void testVarDefFromNestedFunction() throws Exception {
        assertIsValidVarDef();
    }

    public void testGlobalVarDefFromFunction() throws Exception {
        assertIsValidVarDef();
    }

    public void testLocalVarDefFromFunctionError() throws Exception {
        try {
            Assert.assertTrue(configure().resolve() instanceof BashVarDef);
            Assert.fail("The variable resolved, but shouldn't");
        } catch (AssertionFailedError e) {
            //all's fine
        }
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/resolve/varDef/";
    }
}
