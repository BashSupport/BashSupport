/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ConditionalParsingTest.java, Class: ConditionalParsingTest
 * Last modified: 2010-01-19
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

import org.junit.Test;

/**
 * Date: 25.03.2009
 * Time: 22:04:31
 *
 * @author Joachim Ansorg
 */
public class ConditionalParsingTest extends MockPsiTest {
    private final MockFunction conditionalCommandParserTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.shellCommand.conditionalParser.parse(builder);
        }
    };

    @Test
    public void testConditional1() {
        //[ -z a ]
        mockTest(conditionalCommandParserTest, EXPR_CONDITIONAL, COND_OP, WORD, _EXPR_CONDITIONAL);
        //[ a ]
        mockTest(conditionalCommandParserTest, EXPR_CONDITIONAL, WORD, _EXPR_CONDITIONAL);
        //[ $a ]
        mockTest(conditionalCommandParserTest, EXPR_CONDITIONAL, VARIABLE, _EXPR_CONDITIONAL);
        //[ $(a) = a ]
        mockTest(conditionalCommandParserTest, EXPR_CONDITIONAL, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN, EQ, WORD, _EXPR_CONDITIONAL);
        //[[ -z "" ]]
        mockTest(conditionalCommandParserTest, BRACKET_KEYWORD, COND_OP, STRING_BEGIN, STRING_END, _BRACKET_KEYWORD);
        //[[ a ]]
        mockTest(conditionalCommandParserTest, BRACKET_KEYWORD, WORD, _BRACKET_KEYWORD);
        //[[ $(echo a) ]]
        mockTest(conditionalCommandParserTest, BRACKET_KEYWORD, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN, _BRACKET_KEYWORD);
        //[[ `echo a` ]]
        mockTest(conditionalCommandParserTest, EXPR_CONDITIONAL, BACKQUOTE, WORD, WORD, BACKQUOTE, _EXPR_CONDITIONAL);
        //[ \${a} ]
        mockTest(conditionalCommandParserTest, BRACKET_KEYWORD, WORD, LEFT_CURLY, WORD, RIGHT_CURLY, _BRACKET_KEYWORD);
        //[ a  ] 
        mockTest(conditionalCommandParserTest, BRACKET_KEYWORD, WORD, WHITESPACE, _BRACKET_KEYWORD);
        //[[ a  ]]
        mockTest(conditionalCommandParserTest, EXPR_CONDITIONAL, WORD, WHITESPACE, _EXPR_CONDITIONAL);

        //[[ $(a)  ]]
        mockTest(conditionalCommandParserTest, EXPR_CONDITIONAL, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, WHITESPACE, _EXPR_CONDITIONAL);

        //[[ a || b ]]
        mockTest(conditionalCommandParserTest, EXPR_CONDITIONAL, WORD, WHITESPACE, OR_OR, WORD, WHITESPACE, _EXPR_CONDITIONAL);


        //fixme: [ ]
        //mockTest(conditionalCommandParserTest, EXPR_CONDITIONAL, WHITESPACE, _EXPR_CONDITIONAL);
        //fixme:[[ ]]
        //mockTestError(conditionalCommandParserTest, BRACKET_KEYWORD, _BRACKET_KEYWORD);
    }

    @Test
    public void testConditionalError() {
        //[ if a; then b; fi ]
        mockTestError(conditionalCommandParserTest, EXPR_CONDITIONAL, IF_KEYWORD, WORD, SEMI,
                THEN_KEYWORD, WORD, SEMI, FI_KEYWORD, _EXPR_CONDITIONAL);
    }
}
