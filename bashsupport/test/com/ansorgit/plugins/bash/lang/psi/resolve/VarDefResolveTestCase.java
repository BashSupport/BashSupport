/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: VarDefResolveTestCase.java, Class: VarDefResolveTestCase
 * Last modified: 2010-07-12
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
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import junit.framework.Assert;

/**
 * User: jansorg
 * Date: 15.06.2010
 * Time: 19:14:20
 */
public class VarDefResolveTestCase extends AbstractResolveTest {
    private BashVarDef assertIsValidVarDef() throws Exception {
        PsiReference ref = configure();
        PsiElement element = ref.resolve();
        Assert.assertNotNull("The definition could not be resolved.", element);
        Assert.assertTrue("The resolved definition is not a BashVarDef: " + element, element instanceof BashVarDef);
        Assert.assertFalse("The variable must not resolve to itself.", ref.equals(element));
        Assert.assertTrue("isReferenceTo is not working properly.", ref.isReferenceTo(element));

        return (BashVarDef) element;
    }

    private void assertIsInvalidVarDef() throws Exception {
        PsiReference ref = configure();
        Assert.assertNull(ref.resolve());
    }

    public void testGlobalVarDef() throws Exception {
        BashVarDef ref = assertIsValidVarDef();

        //we check on global level, our var def must not resolve this this var def again, because our ref element is after the var def
        PsiElement varDefDef = ref.resolve();
        Assert.assertNull("ref must not resolve to anything else: " + ref, varDefDef);
    }

    public void testRedefineVarDef() throws Exception {
        BashVarDef varDef = assertIsValidVarDef();
        //the variable definition value has to be the remaining part of the line, otherwise the parsing of the value does
        //not properly work
        assertNull(varDef.getNextSibling());
    }

    public void testGlobalVarDefWithLocal() throws Exception {
        assertIsValidVarDef();
    }

    public void testVarDefFromOuterFunction() throws Exception {
        assertIsValidVarDef();
    }

    public void testErrorVarDefFromNestedFunction() throws Exception {
        assertIsInvalidVarDef();
    }

    public void testValidVarDefFromNestedFunction() throws Exception {
        assertIsValidVarDef();
    }

    public void testGlobalVarDefFromFunction() throws Exception {
        assertIsValidVarDef();
    }

    public void testErrorGlobalVarDefFromFunction() throws Exception {
        assertIsInvalidVarDef();
    }

    public void testLocalVarDefFromFunctionError() throws Exception {
        assertIsInvalidVarDef();
    }

    public void testArrayVarDef1() throws Exception {
        assertIsValidVarDef();
    }

    public void testArrayVarDef2() throws Exception {
        assertIsValidVarDef();
    }

    public void testArrayVarDef3() throws Exception {
        assertIsValidVarDef();
    }


    public void testLocalVarDefResolve() throws Exception {
        //the inner var def must not resolve to the global variable definition
        BashVarDef varDef = assertIsValidVarDef();
        Assert.assertTrue(BashPsiUtils.findNextVarDefFunctionDefScope(varDef) != null);
        Assert.assertNull(varDef.resolve());
    }

    public void testResolveFunctionDefToGlobalDef() throws Exception {
        PsiElement varDef = assertIsValidVarDef();
        //the found var def has to be on global level
        Assert.assertTrue(BashPsiUtils.findNextVarDefFunctionDefScope(varDef) == null);
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/resolve/varDef/";
    }
}
