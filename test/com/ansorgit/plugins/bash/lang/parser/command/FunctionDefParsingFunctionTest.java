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

package com.ansorgit.plugins.bash.lang.parser.command;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.google.common.collect.Lists;
import org.junit.Test;

public class FunctionDefParsingFunctionTest extends MockPsiTest {
    private final MockFunction f = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.command.parse(psi);
        }
    };

    @Test
    public void testFunctionDefError() throws Exception {
        //function a b {
        //echo
        //}
        mockTestSuccessWithErrors(f, FUNCTION_KEYWORD, WHITESPACE, WORD, WHITESPACE, WORD, LEFT_CURLY, LINE_FEED, WORD, LINE_FEED, RIGHT_CURLY);
    }

    @Test
    public void testIssue393() throws Exception {
        // function разработка() {
        // echo
        // }
        mockTest(f, Lists.newArrayList("function", "разработка"), FUNCTION_KEYWORD, WHITESPACE, WORD, LEFT_CURLY, LINE_FEED, WORD, LINE_FEED, RIGHT_CURLY);
    }

    @Test
    public void testBodyWithErrors() throws Exception {
        //function a() {
        // echo ${=1}
        //}
        mockTestSuccessWithErrors(f, FUNCTION_KEYWORD, WHITESPACE, WORD, LEFT_PAREN, RIGHT_PAREN, WHITESPACE, LEFT_CURLY, LINE_FEED,
                WORD, DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_UNKNOWN, INTEGER_LITERAL, RIGHT_CURLY, LINE_FEED, RIGHT_CURLY);

        //function a() {
        // echo ${=1}; echo
        //}
        mockTestSuccessWithErrors(f, FUNCTION_KEYWORD, WHITESPACE, WORD, LEFT_PAREN, RIGHT_PAREN, WHITESPACE, LEFT_CURLY, LINE_FEED,
                WORD, DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_UNKNOWN, INTEGER_LITERAL, RIGHT_CURLY, SEMI, WHITESPACE, WORD, LINE_FEED, LINE_FEED, RIGHT_CURLY);
    }

    @Test
    public void testIssue465() throws Exception {
        //function foo() (
        //    :
        //)
        mockTest(f, FUNCTION_KEYWORD, WHITESPACE, WORD, LEFT_PAREN, RIGHT_PAREN, WHITESPACE, LEFT_PAREN, LINE_FEED, WORD, LINE_FEED, RIGHT_PAREN);

        //function foo()
        //
        // (
        //    :
        //)
        mockTest(f, FUNCTION_KEYWORD, WHITESPACE, WORD, LEFT_PAREN, RIGHT_PAREN, LINE_FEED, LINE_FEED, LEFT_PAREN, LINE_FEED, WORD, LINE_FEED, RIGHT_PAREN);

        //foo() (
        //    :
        //)
        mockTest(f, WORD, LEFT_PAREN, RIGHT_PAREN, WHITESPACE, LEFT_PAREN, LINE_FEED, WORD, LINE_FEED, RIGHT_PAREN);

        //foo()
        //
        // (
        //    :
        //)
        mockTest(f, WORD, LEFT_PAREN, RIGHT_PAREN, LINE_FEED, LINE_FEED, LEFT_PAREN, LINE_FEED, WORD, LINE_FEED, RIGHT_PAREN);

        // foo() if true; then
        // :
        // fi
        mockTest(f, WORD, LEFT_PAREN, RIGHT_PAREN, WHITESPACE, IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, LINE_FEED, WORD, LINE_FEED, FI_KEYWORD);
    }
}
