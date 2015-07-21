/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ArithmeticParserTest.java, Class: ArithmeticParserTest
 * Last modified: 2010-05-08
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

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.misc.ShellCommandParsing;
import org.junit.Test;

public class ArithmeticParserTest extends MockPsiTest {
    private final MockFunction arithmeticTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return ShellCommandParsing.arithmeticParser.parse(psi);
        }
    };

    @Test
    public void testParseSquareMode() {
        //[1]
        mockTest(arithmeticTest, EXPR_ARITH_SQUARE, ARITH_NUMBER, _EXPR_ARITH_SQUARE);

        //[a + b]
        mockTest(arithmeticTest, EXPR_ARITH_SQUARE, WORD, ARITH_MINUS, WORD, _EXPR_ARITH_SQUARE);
    }

    @Test
    public void testParse() throws Exception {
        //((1))
        mockTest(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, _EXPR_ARITH);

        //((a))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, _EXPR_ARITH);

        //(($a))
        mockTest(arithmeticTest, EXPR_ARITH, VARIABLE, _EXPR_ARITH);

        //((1 + 1))
        mockTest(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, _EXPR_ARITH);

        //((a + b))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_MINUS, WORD, _EXPR_ARITH);

        //((1 * 2 - 3))
        mockTest(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, ARITH_MULT, ARITH_NUMBER, ARITH_MINUS, ARITH_NUMBER, _EXPR_ARITH);

        //((10 % 3))
        mockTest(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, ARITH_MOD, ARITH_NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testNumbers() {
        //((-1))
        mockTest(arithmeticTest, EXPR_ARITH, ARITH_MINUS, ARITH_NUMBER, _EXPR_ARITH);

        //((--1))
        mockTest(arithmeticTest, EXPR_ARITH, ARITH_MINUS, ARITH_MINUS, ARITH_NUMBER, _EXPR_ARITH);

        //((---a))
        mockTest(arithmeticTest, EXPR_ARITH, ARITH_MINUS, ARITH_MINUS, ARITH_NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testParseList() throws Exception {
        //((1,2))
        mockTest(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, COMMA, ARITH_NUMBER, _EXPR_ARITH);

        //(($a,b))
        mockTest(arithmeticTest, EXPR_ARITH, VARIABLE, COMMA, WORD, _EXPR_ARITH);

        //(($a,1*3,b))
        mockTest(arithmeticTest, EXPR_ARITH, VARIABLE, COMMA, ARITH_NUMBER, ARITH_MULT, ARITH_NUMBER, COMMA, WORD, _EXPR_ARITH);
    }

    @Test
    public void testParseAssignments() throws Exception {
        //((a=1))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, EQ, ARITH_NUMBER, _EXPR_ARITH);

        //((a=1)) with assignment word
        mockTest(arithmeticTest, EXPR_ARITH, ASSIGNMENT_WORD, EQ, ARITH_NUMBER, _EXPR_ARITH);

        //((a += 1))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_ASS_PLUS, ARITH_NUMBER, _EXPR_ARITH);

        //((a -= 1))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_ASS_MINUS, ARITH_NUMBER, _EXPR_ARITH);

        //((a *= 1))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_ASS_MUL, ARITH_NUMBER, _EXPR_ARITH);

        //((a %= 1))
        mockTest(arithmeticTest, EXPR_ARITH, WORD, ARITH_ASS_MOD, ARITH_NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testParseParentheses() throws Exception {
        //(((1)))
        mockTest(arithmeticTest, EXPR_ARITH, LEFT_PAREN, ARITH_NUMBER, RIGHT_PAREN, _EXPR_ARITH);

        //(((1 * 2)))
        mockTest(arithmeticTest, EXPR_ARITH, LEFT_PAREN, ARITH_NUMBER, ARITH_MULT, ARITH_NUMBER, RIGHT_PAREN, _EXPR_ARITH);

        //((1 * (2 + 3)))
        mockTest(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, ARITH_MULT, LEFT_PAREN, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, RIGHT_PAREN, _EXPR_ARITH);

        //(( (2 + 3) / 2 ))
        mockTest(arithmeticTest, EXPR_ARITH, LEFT_PAREN, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, RIGHT_PAREN, ARITH_DIV, ARITH_NUMBER, _EXPR_ARITH);

        //(( (2 + 3) / (1/2 + (3*4) / 5) ))
        mockTest(arithmeticTest, EXPR_ARITH, LEFT_PAREN, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, RIGHT_PAREN, ARITH_DIV,
                LEFT_PAREN,
                ARITH_NUMBER, ARITH_DIV, ARITH_NUMBER, ARITH_PLUS,
                LEFT_PAREN, ARITH_NUMBER, ARITH_MULT, ARITH_NUMBER, RIGHT_PAREN,
                ARITH_DIV, ARITH_NUMBER,
                RIGHT_PAREN,
                _EXPR_ARITH);

        // Error: (( (3) * ))
        mockTestError(arithmeticTest, EXPR_ARITH, LEFT_PAREN, ARITH_NUMBER, RIGHT_PAREN, ARITH_MULT, _EXPR_ARITH);
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
        mockTest(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, ARITH_QMARK, ARITH_NUMBER, ARITH_MULT, ARITH_NUMBER, ARITH_COLON, WORD, _EXPR_ARITH);

        //((1 == 1 ? 0 : 0))
        mockTest(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, ARITH_EQ, ARITH_NUMBER, ARITH_QMARK, ARITH_NUMBER, ARITH_COLON, ARITH_NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testParseAssignmentsErrors() throws Exception {
        //((1=1))
        mockTestError(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, EQ, ARITH_NUMBER, _EXPR_ARITH);

        //((1 += 1))
        mockTestError(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, ARITH_ASS_PLUS, ARITH_NUMBER, _EXPR_ARITH);

        //((1 -= 1))
        mockTestError(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, ARITH_ASS_MINUS, ARITH_NUMBER, _EXPR_ARITH);

        //((1 *= 1))
        mockTestError(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, ARITH_ASS_MUL, ARITH_NUMBER, _EXPR_ARITH);

        //((1 %= 1))
        mockTestError(arithmeticTest, EXPR_ARITH, ARITH_NUMBER, ARITH_ASS_MOD, ARITH_NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testIssue244() throws Exception {
        mockTest(BashVersion.Bash_v4, arithmeticTest, EXPR_ARITH, VARIABLE, VARIABLE, VARIABLE, _EXPR_ARITH);
    }

    @Test
    public void testIssue201() throws Exception {
        // ((!a))
        mockTest(BashVersion.Bash_v4, arithmeticTest, EXPR_ARITH, ARITH_NEGATE, WORD, _EXPR_ARITH);
        // ((!123))
        mockTest(BashVersion.Bash_v4, arithmeticTest, EXPR_ARITH, ARITH_NEGATE, ARITH_NUMBER, _EXPR_ARITH);
        // ((~a))
        mockTest(BashVersion.Bash_v4, arithmeticTest, EXPR_ARITH, ARITH_BITWISE_NEGATE, WORD, _EXPR_ARITH);
        // ((!(a)))
        mockTest(BashVersion.Bash_v4, arithmeticTest, EXPR_ARITH, ARITH_BITWISE_NEGATE, LEFT_PAREN, WORD, RIGHT_PAREN, _EXPR_ARITH);
        // ((!(!a)))
        mockTest(BashVersion.Bash_v4, arithmeticTest, EXPR_ARITH, ARITH_BITWISE_NEGATE, LEFT_PAREN, ARITH_NEGATE, WORD, RIGHT_PAREN, _EXPR_ARITH);
    }
}
