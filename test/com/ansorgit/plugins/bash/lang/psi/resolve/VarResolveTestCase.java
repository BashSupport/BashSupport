/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: VarResolveTestCase.java, Class: VarResolveTestCase
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

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.psi.PsiReference;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;

/**
 * User: jansorg
 * Date: 15.06.2010
 * Time: 19:14:20
 */
public class VarResolveTestCase extends AbstractResolveTest {
    public void testBasicResolve() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashVarDef);
    }

    public void testBasicResolveCurly() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashVarDef);
    }

    public void testBasicResolveCurlyWithDefault() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashVarDef);
    }

    public void testBasicResolveCurlyWithDefaultString() throws Exception {
        PsiReference psiReference = configure();
        Assert.assertTrue(psiReference.resolve() instanceof BashVarDef);
    }

    public void testFunctionAfterDefResolve() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashVarDef);
    }

    public void testFunctionBeforeDefResolve() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashVarDef);
    }

    public void testBasicResolveArithmetic() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashVarDef);
    }

    public void testBasicResolveArithmeticImplicit() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashVarDef);
    }

    public void testBasicResolveLocalVar() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashVarDef);
    }

    public void testBasicResolveLocalVarNested() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashVarDef);
    }

    public void testBasicResolveForLoopVar() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashVarDef);
    }

    public void testBasicResolveForLoopArithVar() throws Exception {
        Assert.assertTrue(configure().resolve() instanceof BashVarDef);
    }

    //fails

    public void testBasicResolveLocalVarGlobal() throws Exception {
        PsiReference psiReference = configure();
        try {
            Assert.assertTrue(psiReference.resolve() instanceof BashVarDef);
            Assert.fail("The local variable must not be resolved on global level.");
        } catch (AssertionFailedError e) {
            //ok
        }
    }

    public void testBasicResolveUnknownVariable() throws Exception {
        PsiReference psiReference = configure();
        try {
            Assert.assertTrue(psiReference.resolve() instanceof BashVarDef);
            Assert.fail("The local variable must not be resolved on global level.");
        } catch (AssertionFailedError e) {
            //ok
        }
    }

    protected String getTestDataPath() {
        return getBasePath() + "/psi/resolve/var/";
    }
}
