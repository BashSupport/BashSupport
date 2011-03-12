/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: CommandParsingTest.java, Class: CommandParsingTest
 * Last modified: 2010-02-10
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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.parser.command.CommandParsingUtil;
import org.junit.Test;

/**
 * Date: 24.03.2009
 * Time: 23:12:42
 *
 * @author Joachim Ansorg
 */
public class CommandParsingTest extends MockPsiTest {
    private MockFunction simpleCommandTest = new MockFunction() {
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.command.parse(builder);
        }
    };

    private MockFunction functionDefTest = new MockFunction() {
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.command.parse(builder);
        }
    };

    private MockFunction assignmentListTest = new MockFunction() {
        public boolean apply(BashPsiBuilder builder) {
            return CommandParsingUtil.parseAssignmentList(builder);
        }
    };

    @Test
    public void testParseSimpleCommand() {
        //a
        mockTest(simpleCommandTest, WORD);
        //{a,a}
        mockTest(simpleCommandTest, LEFT_CURLY, WORD, COMMA, WORD, RIGHT_CURLY);
        //1{a,a}
        mockTest(simpleCommandTest, INTEGER_LITERAL, LEFT_CURLY, WORD, COMMA, WORD, RIGHT_CURLY);
        //$a
        mockTest(simpleCommandTest, VARIABLE);
    }

    @Test
    public void testParseSimpleCommand2() {
        //echo $a
        mockTest(simpleCommandTest, WORD, VARIABLE);
        //tr [:echo:]
        mockTest(simpleCommandTest, WORD, LEFT_SQUARE, WORD);
    }

    @Test
    public void testParseAssignment() {
        //a=a
        mockTest(simpleCommandTest, ASSIGNMENT_WORD, EQ, WORD);
    }

    @Test
    public void testParseSimpleCommandCombined() {
        //a > log
        mockTest(simpleCommandTest, WORD, GREATER_THAN, WORD);
        //a > log >> log2
        mockTest(simpleCommandTest, WORD, GREATER_THAN, WORD, SHIFT_RIGHT, WORD);
    }

    @Test
    public void testParseSimpleCommandCombined3() {
        //echo a >> out c
        mockTest(simpleCommandTest, WORD, WORD, SHIFT_RIGHT, WORD, WORD);
    }

    @Test
    public void testParseSimpleCommandAssignment1() {
        //a=;
        mockTest(simpleCommandTest, 2, ASSIGNMENT_WORD, EQ, SEMI);

        //a=\n
        mockTest(simpleCommandTest, 2, ASSIGNMENT_WORD, EQ, LINE_FEED);

        //a=<EOF>
        mockTest(simpleCommandTest, 2, ASSIGNMENT_WORD, EQ, null);

        //a=1 b=2 echo
        mockTest(simpleCommandTest, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, INTERNAL_COMMAND);

        //a=1 b=2
        mockTest(simpleCommandTest, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL);

        //a=1 b=2, white whitespace
        mockTest(simpleCommandTest, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, WHITESPACE, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL);
    }

    @Test
    public void testParseSimpleCommandCombinedError() { //error case
        mockTest(simpleCommandTest, 1, WORD, PIPE, WORD); //a | b
    }

    @Test
    public void testFunctionDefinition() {
        //function a() { echo b; }
        mockTest(functionDefTest, FUNCTION_KEYWORD, WORD, LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, WHITESPACE, WORD, SEMI, RIGHT_CURLY);

        //function a() { 
        //echo b; }
        mockTest(functionDefTest, FUNCTION_KEYWORD, WORD, LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, LINE_FEED, WORD, SEMI, RIGHT_CURLY);

        //a() { echo b; }
        mockTest(functionDefTest,
                WORD, LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, WHITESPACE, WORD, SEMI, RIGHT_CURLY);
    }

    @Test
    public void testParseAssignment1() {
        //(1 2 3)
        mockTest(assignmentListTest, LEFT_PAREN, INTEGER_LITERAL, WHITESPACE, INTEGER_LITERAL, WHITESPACE, INTEGER_LITERAL, RIGHT_PAREN);

        //([1]=2)
        mockTest(assignmentListTest, LEFT_PAREN, LEFT_SQUARE, NUMBER, RIGHT_SQUARE, EQ, INTEGER_LITERAL, RIGHT_PAREN);

        //([1]=2,2,3)
        mockTest(assignmentListTest, LEFT_PAREN, LEFT_SQUARE, NUMBER, RIGHT_SQUARE, EQ, INTEGER_LITERAL, WHITESPACE, INTEGER_LITERAL, WHITESPACE, INTEGER_LITERAL, RIGHT_PAREN);

        //("a")
        mockTest(assignmentListTest, LEFT_PAREN, STRING_BEGIN, WORD, STRING_END, RIGHT_PAREN);

        //(a, "a")
        mockTest(assignmentListTest, LEFT_PAREN, WORD, WHITESPACE, STRING_BEGIN, WORD, STRING_END, RIGHT_PAREN);

        //(a, "a" "b" c d)
        mockTest(assignmentListTest, LEFT_PAREN, WORD, WHITESPACE, STRING_BEGIN, WORD, STRING_END, STRING_BEGIN, WORD, STRING_END, WORD, WORD, RIGHT_PAREN);

        //(
        // a
        // )
        mockTest(assignmentListTest, LEFT_PAREN, LINE_FEED, WORD, LINE_FEED, RIGHT_PAREN);

        //(
        // a,
        // )
        mockTest(assignmentListTest, LEFT_PAREN, LINE_FEED, WORD, WHITESPACE, LINE_FEED, RIGHT_PAREN);

        //(
        // a=a
        // )
        mockTest(assignmentListTest, LEFT_PAREN, LINE_FEED, WORD, EQ, WORD, LINE_FEED, RIGHT_PAREN);

        //(
        // $(echo 1)
        // )
        mockTest(assignmentListTest, LEFT_PAREN, LINE_FEED,
                DOLLAR, LEFT_PAREN, INTERNAL_COMMAND, WORD, RIGHT_PAREN,
                LINE_FEED, RIGHT_PAREN);

        //now test as simple command
        //a=([1]=2 2 3)
        mockTest(simpleCommandTest, ASSIGNMENT_WORD, EQ, LEFT_PAREN, LEFT_SQUARE, NUMBER, RIGHT_SQUARE, EQ, INTEGER_LITERAL, WHITESPACE, INTEGER_LITERAL, WHITESPACE, INTEGER_LITERAL, RIGHT_PAREN);
    }

    @Test
    public void testNestedIf() {
        //a() {
        //   if a; then b; fi
        //   if b; then b; fi
        //}
        mockTest(functionDefTest,
                WORD, LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, LINE_FEED,
                IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, SEMI, FI_KEYWORD, LINE_FEED,
                IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, SEMI, FI_KEYWORD, LINE_FEED,
                RIGHT_CURLY);
    }


    @Test
    public void testParseCombinedCommand() {
        //ssh ${GROUND_USER}@${GROUND}
        mockTest(simpleCommandTest, WORD, WHITESPACE, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, AT, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
    }

    @Test
    public void testParseArrayAssignment() throws Exception {
        //a=(a [b]=x z)
        mockTest(simpleCommandTest, ASSIGNMENT_WORD, EQ, LEFT_PAREN, WORD, WHITESPACE, LEFT_SQUARE, WORD, RIGHT_SQUARE, EQ, WORD, WHITESPACE, WORD, RIGHT_PAREN);
    }
}
