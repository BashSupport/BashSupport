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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jansorg
 */
public class ConditionalCommandParsingFunctionTest extends MockPsiTest {
    MockFunction conditionalFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.conditionalCommandParser.parse(psi);
        }
    };

    @Test
    public void testSingleTest() {
        //[[ a ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, _BRACKET_KEYWORD);
    }

    @Test
    public void testComposedCommand() {
        //[[ a && b ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, AND_AND, WORD, _BRACKET_KEYWORD);

        //[[ a || b ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, OR_OR, WORD, _BRACKET_KEYWORD);

        //[[ a || b && c ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, OR_OR, WORD, AND_AND, WORD, _BRACKET_KEYWORD);

        //[[ -z "" ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, COND_OP, STRING_BEGIN, STRING_END, _BRACKET_KEYWORD);

        //[[ a ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, _BRACKET_KEYWORD);

        //[[ $(echo a) ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WHITESPACE, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN, WHITESPACE, _BRACKET_KEYWORD);
    }

    @Test
    @Ignore
    public void testRegExp() throws Exception {
        //[[ a =~ abc ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, WHITESPACE, COND_OP_REGEX, WHITESPACE, WORD, _BRACKET_KEYWORD);

        //[[ $(break_request) =~ Denied ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, WHITESPACE, COND_OP_REGEX, WHITESPACE, WORD, _BRACKET_KEYWORD);

        //[[ a =~ ..e*x ]]
        //mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, WHITESPACE, COND_OP_REGEX, WHITESPACE, WORD, _BRACKET_KEYWORD);

        //[[ a =~ ^$ ]]
        //mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, WHITESPACE, COND_OP_REGEX, WHITESPACE, WORD, _BRACKET_KEYWORD);
    }

    @Test
    public void testErrors() throws Exception {
        //fixme: [ ]
        //mockTest(conditionalCommandParserTest, EXPR_CONDITIONAL, WHITESPACE, _EXPR_CONDITIONAL);
        //fixme:[[ ]]
        //mockTestError(conditionalCommandParserTest, BRACKET_KEYWORD, _BRACKET_KEYWORD);
    }

    @Test
    public void testNegationOperator() {
        //[[ !(a) ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, COND_OP_NOT, LEFT_PAREN, WORD, RIGHT_PAREN, _BRACKET_KEYWORD);
    }

    @Test
    public void testIssue367() throws Exception {
        //[[ $(< $1) ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, DOLLAR, LEFT_PAREN, LESS_THAN, WHITESPACE, VARIABLE, RIGHT_PAREN, _BRACKET_KEYWORD);
    }

    @Test
    public void testIssue412() throws Exception {
        mockTest(conditionalFunction, BRACKET_KEYWORD, LEFT_PAREN, WORD, WHITESPACE, COND_OP_REGEX, WHITESPACE, STRING_BEGIN, STRING_CONTENT, STRING_END, RIGHT_PAREN, _BRACKET_KEYWORD);
    }
}
