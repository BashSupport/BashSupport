/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: StaticValueComputationTest.java, Class: StaticValueComputationTest
 * Last modified: 2010-07-17
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

package com.ansorgit.plugins.bash.lang.psi.arithmetic;

import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import org.junit.Assert;

/**
 * User: jansorg
 * Date: 17.07.2010
 * Time: 10:27:57
 */
public class StaticValueComputationTest extends AbstractArithExprTest {
    public void testExpressionTest() throws Exception {
        assertValueComputedResult(74430296);
    }

    public void testProductOfSumsTest() throws Exception {
        assertValueComputedResult(696);
    }

    public void testComplicatedExpressionTest() throws Exception {
        assertValueComputedResult(3035138555904L);
    }

    public void testNegativeComplicatedExpressionTest() throws Exception {
        assertValueComputedResult(-3035138555904L);
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
