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

package com.ansorgit.plugins.bash.lang.psi.arithmetic;

import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jansorg
 */
public class StaticValueComputationTest extends AbstractArithExprTest {
    @Test
    public void testExpressionTest() throws Exception {
        assertValueComputedResult(74430296);
    }

    @Test
    public void testProductOfSumsTest() throws Exception {
        assertValueComputedResult(696);
    }

    @Test
    public void testComplicatedExpressionTest() throws Exception {
        assertValueComputedResult(3035138555904L);
    }

    @Test
    public void testNegativeComplicatedExpressionTest() throws Exception {
        assertValueComputedResult(-3035138555904L);
    }

    @Test
    public void testBaseValueTest() throws Exception {
        assertValueComputedResult(168);
    }

    @Test
    public void testHexBaseValueTest() throws Exception {
        assertValueComputedResult(2748);
    }

    public void testAssignmentChain() throws Exception {
        //issue #420
        try {
            assertValueComputedResult(-1);
            Assert.fail("the value computation must fail with a UnsupportedOperationException");
        } catch (UnsupportedOperationException ignored) {
        }
    }

    private void assertValueComputedResult(long expectedValue) throws Exception {
        ArithmeticExpression start = configureTopArithExpression();

        Assert.assertEquals("Invalid value computed", expectedValue, start.computeNumericValue());
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "value/";
    }

}
