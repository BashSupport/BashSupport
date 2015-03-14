/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: WordParsingTest.java, Class: WordParsingTest
 * Last modified: 2009-12-04
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
 * Date: 26.03.2009
 * Time: 16:13:05
 *
 * @author Joachim Ansorg
 */
public class WordParsingTest extends MockPsiTest {
    private MockFunction wordTestParser = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.word.parseWord(builder);
        }
    };

    @Test
    public void testParseWord1() {
        mockTest(wordTestParser, WORD);
    }

    @Test
    public void testSingleBangToken() throws Exception {
        mockTest(wordTestParser, BANG_TOKEN);
    }

    @Test
    public void testParseWord2() {
        //$(echo a)
        mockTest(wordTestParser, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN);
    }

    @Test
    public void testParseWord3() {
        mockTest(wordTestParser, DOLLAR, EXPR_ARITH, NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testParseEmbeddedVar() {
        //"$a"
        mockTest(wordTestParser, STRING_BEGIN, VARIABLE, STRING_END);
        //"$(echo)"
        mockTest(wordTestParser, STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, STRING_END);
        //"$(123)"
        mockTest(wordTestParser, STRING_BEGIN, DOLLAR, LEFT_PAREN, INTEGER_LITERAL, RIGHT_PAREN, STRING_END);
        //"$((a))"
        mockTest(wordTestParser, STRING_BEGIN, DOLLAR, EXPR_ARITH, WORD, _EXPR_ARITH, STRING_END);
    }

    @Test
    public void testExpansions() throws Exception {
        //{}
        mockTest(wordTestParser, LEFT_CURLY, RIGHT_CURLY);
        //a{a}
        mockTest(wordTestParser, WORD, LEFT_CURLY, WORD, RIGHT_CURLY);
        //{a}
        mockTest(wordTestParser, LEFT_CURLY, WORD, RIGHT_CURLY);
        //{1}
        mockTest(wordTestParser, LEFT_CURLY, INTEGER_LITERAL, RIGHT_CURLY);
        //{1,a}
        mockTest(wordTestParser, LEFT_CURLY, INTEGER_LITERAL, COMMA, WORD, RIGHT_CURLY);
        //{1,a,2}z
        mockTest(wordTestParser, LEFT_CURLY, INTEGER_LITERAL, COMMA, WORD, COMMA, INTEGER_LITERAL, RIGHT_CURLY, WORD);
    }

}
