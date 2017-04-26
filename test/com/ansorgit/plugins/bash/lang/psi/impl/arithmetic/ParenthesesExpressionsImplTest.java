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

package com.ansorgit.plugins.bash.lang.psi.impl.arithmetic;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ParenthesesExpression;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class ParenthesesExpressionsImplTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testCompute() {
        ParenthesesExpression e = createDummyNode("$(( (42 * 5) ))");
        Assert.assertTrue(e.isStatic());
        Assert.assertEquals(42 * 5, e.computeNumericValue());
    }

    @Test
    public void testSimple() throws Exception {
        ParenthesesExpression e = createDummyNode("$(( (1) ))");
        Assert.assertEquals(1, e.computeNumericValue());
        Assert.assertTrue(e.isStatic());
    }

    @Test
    public void testNonStatic() throws Exception {
        ParenthesesExpression e = createDummyNode("$(( (abc) ))");
        Assert.assertFalse(e.isStatic());

        try {
            e.computeNumericValue();
            Assert.fail("Expected InvalidExpressionValue exception");
        } catch (InvalidExpressionValue ex) {
            //ignore
        }
    }

    @Test
    public void testNonStatic2() throws Exception {
        ParenthesesExpression e = createDummyNode("$(( (1 + abc) ))");
        Assert.assertFalse(e.isStatic());

        try {
            e.computeNumericValue();
            Assert.fail("Expected InvalidExpressionValue exception");
        } catch (InvalidExpressionValue ex) {
            //ignore
        }
    }

    private ParenthesesExpression createDummyNode(String expressionText) {
        PsiFile psiFile = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, expressionText);

        return PsiTreeUtil.findChildOfType(psiFile, ParenthesesExpression.class);
    }

    @Override
    protected boolean isWriteActionRequired() {
        return false;
    }
}