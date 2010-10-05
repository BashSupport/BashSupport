/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: VarResolveTestCase.java, Class: VarResolveTestCase
 * Last modified: 2010-07-13
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
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReference;
import junit.framework.Assert;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: jansorg
 * Date: 15.06.2010
 * Time: 19:14:20
 */
public class VarResolveTestCase extends AbstractResolveTest {
    private BashVarDef assertIsWellDefinedVariable() throws Exception {
        PsiReference start = configure();
        PsiElement varDef = start.resolve();
        Assert.assertNotNull(varDef);
        Assert.assertTrue(varDef instanceof BashVarDef);
        Assert.assertTrue(start.isReferenceTo(varDef));

        return (BashVarDef) varDef;
    }

    public void testBasicResolve() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testBasicResolveCurly() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testBasicResolveCurlyWithDefault() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testBasicResolveCurlyWithDefaultString() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testFunctionAfterDefResolve() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testFunctionBeforeDefResolve() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testBasicResolveArithmetic() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testBasicResolveArithmeticImplicit() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testBasicResolveLocalVar() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testBasicResolveLocalVarToLocalDef() throws Exception {
        BashVarDef def = assertIsWellDefinedVariable();
        Assert.assertNotNull(BashPsiUtils.findNextVarDefFunctionDefScope(def));
        Assert.assertTrue(def.isFunctionScopeLocal());
    }

    public void testBasicResolveLocalVarNested() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testBasicResolveForLoopVar() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testBasicResolveForLoopArithVar() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testResolveFunctionVarOnGlobal() throws Exception {
        assertIsWellDefinedVariable();
    }

    public void testOverrideFunctionVarOnGlobal() throws Exception {
        PsiElement varDef = assertIsWellDefinedVariable();
        //the found var def has to be on global level
        Assert.assertTrue(BashPsiUtils.findNextVarDefFunctionDefScope(varDef) == null);
    }

    public void testResolveFunctionVarToGlobalDef() throws Exception {
        PsiElement varDef = assertIsWellDefinedVariable();
        //the found var def has to be on global level
        Assert.assertTrue(BashPsiUtils.findNextVarDefFunctionDefScope(varDef) == null);
    }

    public void testResolveFunctionVarToFirstOnSameLevel() throws Exception {
        BashVar varDef = (BashVar) assertIsWellDefinedVariable();
        Assert.assertTrue(varDef.resolve() == null);
    }

    public void testResolveFunctionVarToFirstOnSameLevelNonLocal() throws Exception {
        BashVar varDef = (BashVar) assertIsWellDefinedVariable();
        Assert.assertTrue(varDef.resolve() == null);
    }

    public void testResolveFunctionVarToLocalDef() throws Exception {
        BashVar varDef = (BashVar) assertIsWellDefinedVariable();
        Assert.assertTrue(BashPsiUtils.findBroadestVarDefFunctionDefScope(varDef) != null);
        Assert.assertTrue(varDef.resolve() == null);
    }

    //invalid resolves

    public void testBasicResolveLocalVarGlobal() throws Exception {
        PsiReference psiReference = configure();
        Assert.assertNull("The local variable must not be resolved on global level.", psiReference.resolve());
    }

    public void testBasicResolveUnknownVariable() throws Exception {
        PsiReference psiReference = configure();
        Assert.assertNull("The local variable must not be resolved on global level.", psiReference.resolve());
    }

    public void testNoResolveVarWithTwoLocalDefs() throws Exception {
        PsiReference psiReference = configure();

        //must not resolve because the definition is local due to the previous definition
        PsiElement varDef = psiReference.resolve();
        Assert.assertNull("The vardef should not be found, because it is local", varDef);
    }

    public void testBasicResolveUnknownGlobalVariable() throws Exception {
        final PsiReference psiReference = configure();

        //must not resolve because the definition is local due to the previous definition
        PsiElement varDef = psiReference.resolve();
        Assert.assertNull("The vardef should not be found, because it is undefined", varDef);

        //the variable must not be a valid reference to the following var def

        final AtomicInteger visited = new AtomicInteger(0);
        psiReference.getElement().getContainingFile().acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof BashVarDef) {
                    visited.incrementAndGet();
                    Assert.assertFalse("A var def must not be a valid definition for the variable used.",
                            psiReference.isReferenceTo(element));
                }

                super.visitElement(element);
            }
        });
        Assert.assertEquals(1, visited.get());
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/resolve/var/";
    }
}
