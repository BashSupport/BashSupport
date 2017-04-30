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
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.DumbServiceImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jansorg
 */
public class FunctionResolveTestCase extends AbstractResolveTest {
    @Test
    public void testBasicFunctionResolve() throws Exception {
        checkFunctionReference();
    }

    private PsiElement checkFunctionReference() throws Exception {
        PsiElement functionSmart = doCheckFunctionReference(false);
        PsiElement functionDumb = doCheckFunctionReference(true);

        Assert.assertNotNull(functionSmart);
        Assert.assertNotNull(functionDumb);

        //simple equality check
        Assert.assertEquals(functionSmart.getText(), functionDumb.getText());
        Assert.assertEquals(functionSmart.getTextRange(), functionDumb.getTextRange());

        return functionSmart;
    }

    @NotNull
    private PsiElement doCheckFunctionReference(boolean dumbMode) throws Exception {
        boolean oldDumb = DumbService.isDumb(myProject);

        DumbServiceImpl.getInstance(myProject).setDumb(dumbMode);
        try {
            PsiReference psiReference = configure();
            Assert.assertTrue(psiReference.getElement() instanceof BashCommand);
            BashCommand commandElement = (BashCommand) psiReference.getElement();

            Assert.assertTrue(psiReference.resolve() instanceof BashFunctionDef);
            Assert.assertTrue(commandElement.isFunctionCall());
            Assert.assertFalse(commandElement.isVarDefCommand());
            Assert.assertFalse(commandElement.isExternalCommand());
            Assert.assertTrue(commandElement.getReference().isReferenceTo(psiReference.resolve()));

            return psiReference.resolve();
        } finally {
            DumbServiceImpl.getInstance(myProject).setDumb(oldDumb);
        }
    }

    @Test
    public void testBasicFunctionResolveSelf() throws Exception {
        checkFunctionReference();
    }

    @Test
    public void testBasicFunctionResolveInner() throws Exception {
        checkFunctionReference();
    }

    @Test
    public void testBasicFunctionResolveToLaterDef() throws Exception {
        checkFunctionReference();
    }

    @Test
    public void testInternalCommandOverride() throws Exception {
        checkFunctionReference();
    }

    @Test
    public void testTrapFunctionResolve() throws Exception {
        checkFunctionReference();
    }

    @Test
    public void testNestedFunctionResolve() throws Exception {
        checkFunctionReference();
    }

    @Test
    public void testBasicFunctionResolveToFirstDef() throws Exception {
        checkFunctionReference();

        PsiReference element = configure();
        PsiElement def = element.resolve();
        Assert.assertTrue(def.getTextOffset() < element.getElement().getTextOffset());
    }

    @Test
    public void testFunctionUnicodeNameRussian() throws Exception {
        checkFunctionReference();
    }

    @Test
    public void testFunctionUnicodeNameGreek() throws Exception {
        checkFunctionReference();
    }

    @Test
    public void testFunctionDefinitionWithErrors() throws Exception {
        checkFunctionReference();
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/resolve/function/";
    }
}
