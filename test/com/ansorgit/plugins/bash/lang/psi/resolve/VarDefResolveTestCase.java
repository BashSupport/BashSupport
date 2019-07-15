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

package com.ansorgit.plugins.bash.lang.psi.resolve;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jansorg
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

    @Test
    public void testGlobalVarDef() throws Exception {
        BashVarDef ref = assertIsValidVarDef();

        //we check on global level, our var def must not resolve this this var def again, because our ref element is after the var def
        PsiElement varDefDef = ref.getReference().resolve();
        Assert.assertNull("ref must not resolve to anything else: " + ref, varDefDef);
    }

    @Test
    public void testRedefineVarDef() throws Exception {
        BashVarDef varDef = assertIsValidVarDef();
        //the variable definition value has to be the remaining part of the line, otherwise the parsing of the value does
        //not properly work
        assertNull(varDef.getNextSibling());
    }

    @Test
    public void testGlobalVarDefWithLocal() throws Exception {
        assertIsValidVarDef();
    }

    @Test
    public void testVarDefFromOuterFunction() throws Exception {
        assertIsValidVarDef();
    }

    @Test
    public void testErrorVarDefFromNestedFunction() throws Exception {
        assertIsInvalidVarDef();
    }

    @Test
    public void testValidVarDefFromNestedFunction() throws Exception {
        assertIsValidVarDef();
    }

    @Test
    public void testGlobalVarDefFromFunction() throws Exception {
        assertIsValidVarDef();
    }

    @Test
    public void testErrorGlobalVarDefFromFunction() throws Exception {
        assertIsInvalidVarDef();
    }

    @Test
    public void testLocalVarDefFromFunctionError() throws Exception {
        assertIsInvalidVarDef();
    }

    @Test
    public void testArrayVarDef1() throws Exception {
        assertIsValidVarDef();
    }

    @Test
    public void testArrayVarDef2() throws Exception {
        assertIsValidVarDef();
    }

    @Test
    public void testArrayVarDef3() throws Exception {
        assertIsValidVarDef();
    }

    @Test
    public void testErrorFunctionVarDef() throws Exception {
        BashProjectSettings.storedSettings(myProject).setGlobalFunctionVarDefs(false);
        assertIsInvalidVarDef();
    }

    @Test
    public void testErrorDoubleFunctionVarDef() throws Exception {
        BashProjectSettings.storedSettings(myProject).setGlobalFunctionVarDefs(false);
        assertIsInvalidVarDef();
    }

    @Test
    public void testFunctionVarDef() throws Exception {
        BashProjectSettings.storedSettings(myProject).setGlobalFunctionVarDefs(true);
        assertIsValidVarDef();
    }

    @Test
    public void testNestedMultipleVarDef() throws Exception {
        BashVarDef varDef = assertIsValidVarDef();

        //the resolved var def has to be the first in the file. The problem was, that the second definition was found as the reference
        Assert.assertNull(varDef.getReference().resolve());
    }

    @Test
    public void testNestedMultipleVarDef2() throws Exception {
        BashVarDef varDef = assertIsValidVarDef();

        //the resolved var def has to be the first in the file. The problem was, that the second definition was found as the reference
        Assert.assertNull(varDef.getReference().resolve());
    }

    @Test
    public void testLocalVarDefResolve() throws Exception {
        BashVarDef varDef = assertIsValidVarDef();
        Assert.assertNotNull("The start must be in a function", BashPsiUtils.findNextVarDefFunctionDefScope(varDef));

        PsiElement resolveTarget = varDef.getReference().resolve();
        Assert.assertNull("The local variable def must not resolve to the global variable", resolveTarget);
    }

    @Test
    public void testResolveFunctionDefToGlobalDef() throws Exception {
        PsiElement varDef = assertIsValidVarDef();
        //the found var def has to be on global level
        Assert.assertTrue(BashPsiUtils.findNextVarDefFunctionDefScope(varDef) == null);
    }

    @Test
    public void testIssue262() throws Exception {
        BashVarDef varDef = assertIsValidVarDef();
        //the found var def has to be on global level
        Assert.assertTrue(varDef.isLocalVarDef());
    }

    @Test
    public void testIssue262Typeset() throws Exception {
        BashVarDef varDef = assertIsValidVarDef();
        //the found var def has to be on global level
        Assert.assertTrue(varDef.isLocalVarDef());
    }

    @Test
    public void testIssue262NoGlobalResolve() throws Exception {
        //declare used in a function defines a local variable
        assertIsInvalidVarDef();
    }

    @Test
    public void testIssue262TypesetNoGlobalResolve() throws Exception {
        //declare used in a function defines a local variable
        assertIsInvalidVarDef();
    }

    @Test
    public void testIssue457LineContinuation() throws Exception {
        assertIsValidVarDef();
    }

    @Test
    public void testPrintfDef() throws Exception {
        assertIsValidVarDef();
    }

    @Test
    public void testPrintfDefQuoted() throws Exception {
        assertIsValidVarDef();
    }

    @Test
    public void testPrintfNoVar() throws Exception {
        assertIsInvalidVarDef();
    }

    @Test
    public void testPrintfNoArg() throws Exception {
        assertIsInvalidVarDef();
    }


    @Test
    public void testIssue735_ForLoopDefResolve() throws Exception {
        BashVarDef def = assertIsValidVarDef();
        Assert.assertEquals("This must resolve to the for loop definition", 4, def.getTextOffset());
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/resolve/varDef/";
    }
}
