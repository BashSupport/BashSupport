/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: FunctionResolveTestCase.java, Class: FunctionResolveTestCase
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
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import junit.framework.Assert;

/**
 * User: jansorg
 * Date: 15.06.2010
 * Time: 19:14:20
 */
public class FunctionResolveTestCase extends AbstractResolveTest {
    public void testBasicFunctionResolve() throws Exception {
        checkFunctionReference();
    }

    private PsiReference checkFunctionReference() throws Exception {
        PsiReference psiReference = configure();
        Assert.assertTrue(psiReference.getElement() instanceof BashCommand);
        BashCommand commandElement = (BashCommand) psiReference.getElement();

        Assert.assertTrue(psiReference.resolve() instanceof BashFunctionDef);
        Assert.assertTrue(commandElement.isFunctionCall());
        Assert.assertFalse(commandElement.isInternalCommand());
        Assert.assertFalse(commandElement.isVarDefCommand());
        Assert.assertFalse(commandElement.isExternalCommand());
        Assert.assertTrue(commandElement.isReferenceTo(psiReference.resolve()));

        return psiReference;
    }

    public void testBasicFunctionResolveSelf() throws Exception {
        checkFunctionReference();
    }

    public void testBasicFunctionResolveInner() throws Exception {
        checkFunctionReference();
    }

    public void testBasicFunctionResolveToLaterDef() throws Exception {
        checkFunctionReference();
    }

    public void testBasicFunctionResolveToFirstDef() throws Exception {
        checkFunctionReference();

        PsiReference element = configure();
        PsiElement def = element.resolve();
        Assert.assertTrue(def.getTextOffset() < element.getElement().getTextOffset());
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/resolve/function/";
    }
}
