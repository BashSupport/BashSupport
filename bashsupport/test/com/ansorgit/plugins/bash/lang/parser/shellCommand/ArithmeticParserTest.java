/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ArithmeticParserTest.java, Class: ArithmeticParserTest
 * Last modified: 2010-02-07
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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

public class ArithmeticParserTest extends MockPsiTest {
    private final MockFunction arithmeticTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.arithmeticParser.parse(psi);
        }
    };

    @Test
    public void testParse() throws Exception {
        //((1))
        mockTest(arithmeticTest, EXPR_ARITH, NUMBER, _EXPR_ARITH);

        //((a))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, _EXPR_ARITH);

        //(($a))
        mockTest(arithmeticTest, EXPR_ARITH, VARIABLE, _EXPR_ARITH);

        //((1 + 1))
        mockTest(arithmeticTest, EXPR_ARITH, NUMBER, ARITH_PLUS, NUMBER, _EXPR_ARITH);

        //((a + b))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_MINUS, WORD, _EXPR_ARITH);

        //((1 * 2 - 3))
        mockTest(arithmeticTest, EXPR_ARITH, NUMBER, ARITH_MULT, NUMBER, ARITH_MINUS, NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testParseList() throws Exception {
        //((1,2))
        mockTest(arithmeticTest, EXPR_ARITH, NUMBER, COMMA, NUMBER, _EXPR_ARITH);

        //(($a,b))
        mockTest(arithmeticTest, EXPR_ARITH, VARIABLE, COMMA, WORD, _EXPR_ARITH);

        //(($a,1*3,b))
        mockTest(arithmeticTest, EXPR_ARITH, VARIABLE, COMMA, NUMBER, ARITH_MULT, NUMBER, COMMA, WORD, _EXPR_ARITH);
    }

    @Test
    public void testParseAssignments() throws Exception {
        //((a=1))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, EQ, NUMBER, _EXPR_ARITH);

        //((a=1)) with assignment word
        mockTest(arithmeticTest, EXPR_ARITH, ASSIGNMENT_WORD, EQ, NUMBER, _EXPR_ARITH);

        //((a += 1))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_ASS_PLUS, NUMBER, _EXPR_ARITH);

        //((a -= 1))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_ASS_MINUS, NUMBER, _EXPR_ARITH);

        //((a *= 1))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_ASS_MUL, NUMBER, _EXPR_ARITH);

        //((a %= 1))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_ASS_MOD, NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testParseParentheses() throws Exception {
        //(((1)))
        mockTest(arithmeticTest, EXPR_ARITH, LEFT_PAREN, NUMBER, RIGHT_PAREN, _EXPR_ARITH);

        //(((1 * 2)))
        mockTest(arithmeticTest, EXPR_ARITH, LEFT_PAREN, NUMBER, ARITH_MULT, NUMBER, RIGHT_PAREN, _EXPR_ARITH);

        //((1 * (2 + 3)))
        mockTest(arithmeticTest, EXPR_ARITH, NUMBER, ARITH_MULT, LEFT_PAREN, NUMBER, ARITH_PLUS, NUMBER, RIGHT_PAREN, _EXPR_ARITH);
    }

    @Test
    public void testTristateOperator() {
        //((a ? b : c))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_QMARK, WORD, ARITH_COLON, WORD, _EXPR_ARITH);

        //((a ? (b ? c :d) : c))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_QMARK, LEFT_PAREN, WORD, ARITH_QMARK, WORD, ARITH_COLON, WORD, RIGHT_PAREN, ARITH_COLON, WORD, _EXPR_ARITH);

        //((a ? b ? c :d : c))
        //mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_QMARK, WORD, ARITH_QMARK, WORD, ARITH_COLON, WORD, ARITH_COLON, WORD, _EXPR_ARITH);

        //((1+2 ? 3*4 : 5))
        mockTest(arithmeticTest, EXPR_ARITH, NUMBER, ARITH_PLUS, NUMBER, ARITH_QMARK, NUMBER, ARITH_MULT, NUMBER, ARITH_COLON, WORD, _EXPR_ARITH);
    }

    @Test
    public void testParseAssignmentsErrors() throws Exception {
        //((1=1))
        mockTestError(arithmeticTest, EXPR_ARITH, NUMBER, EQ, NUMBER, _EXPR_ARITH);

        //((1 += 1))
        mockTestError(arithmeticTest, EXPR_ARITH, NUMBER, ARITH_ASS_PLUS, NUMBER, _EXPR_ARITH);

        //((1 -= 1))
        mockTestError(arithmeticTest, EXPR_ARITH, NUMBER, ARITH_ASS_MINUS, NUMBER, _EXPR_ARITH);

        //((1 *= 1))
        mockTestError(arithmeticTest, EXPR_ARITH, NUMBER, ARITH_ASS_MUL, NUMBER, _EXPR_ARITH);

        //((1 %= 1))
        mockTestError(arithmeticTest, EXPR_ARITH, NUMBER, ARITH_ASS_MOD, NUMBER, _EXPR_ARITH);
    }
}
