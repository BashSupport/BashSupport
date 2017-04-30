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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * @author jansorg
 */
public class EvaluateArithmeticExpressionInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/ok", EvaluateArithmeticExpressionInspection.class, true);
    }

    @Test
    public void testErrors() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/errors", EvaluateArithmeticExpressionInspection.class, true);
    }

    @Test
    public void testEvaluation() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/evaluation", EvaluateArithmeticExpressionInspection.class, true);
    }

    @Test
    public void testDivByZero() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/divByZero", EvaluateArithmeticExpressionInspection.class, true);
    }
}
