/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ArithmeticExprParserTest.java, Class: ArithmeticExprParserTest
 * Last modified: 2010-05-27
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

package com.ansorgit.plugins.bash.lang.parser.arithmetic;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 27.05.2010
 * Time: 20:18:34
 */
public class ArithmeticExprParserTest extends MockPsiTest {
    MockFunction exprParser = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return ArithmeticFactory.entryPoint().parse(psi);
        }
    };

    @Test
    public void testVarsInNumbers() {
        mockTest(exprParser, NUMBER);
        mockTest(exprParser, ARITH_HEX_NUMBER);
        mockTest(exprParser, ARITH_OCTAL_NUMBER);
        mockTest(exprParser, ARITH_BASE_NUMBER);

        mockTest(exprParser, NUMBER, VARIABLE);
        mockTest(exprParser, NUMBER, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, NUMBER);
        mockTest(exprParser, NUMBER, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, NUMBER, WHITESPACE, ARITH_DIV, WHITESPACE, NUMBER);
        mockTest(exprParser, NUMBER, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, NUMBER, WHITESPACE, ARITH_DIV, WHITESPACE, NUMBER, VARIABLE);
    }

    @Test
    public void testWhitespace() throws Exception {
        mockTest(exprParser, NUMBER, WHITESPACE, ARITH_SHIFT_LEFT, WHITESPACE, NUMBER);
    }

    @Test
    public void testComplexExpressions() throws Exception {
        //1 < "x"
        mockTest(exprParser, NUMBER, WHITESPACE, ARITH_LT, WHITESPACE, STRING_BEGIN, WORD, STRING_END);

        //1 < $(a)
        mockTest(exprParser, NUMBER, WHITESPACE, ARITH_LT, WHITESPACE, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN);
    }
}
