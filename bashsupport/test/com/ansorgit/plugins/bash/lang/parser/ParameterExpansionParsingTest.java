/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ParameterExpansionParsingTest.java, Class: ParameterExpansionParsingTest
 * Last modified: 2010-07-01
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
 * User: jansorg
 * Date: Jan 27, 2010
 * Time: 9:00:48 PM
 */
public class ParameterExpansionParsingTest extends MockPsiTest {
    MockFunction expansionParser = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.parameterExpansionParsing.parse(psi);
        }
    };

    @Test
    public void testParse() throws Exception {
        //{A}
        mockTest(expansionParser, LEFT_CURLY, WORD, RIGHT_CURLY);

        //{B:-B}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP, PARAM_EXPANSION_OP, WORD, RIGHT_CURLY);
    }

    @Test
    public void testParseAdvanced() {
        //{a:$(a $(b)/..)}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP, DOLLAR, LEFT_PAREN, WORD,
                WHITESPACE, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN,
                WORD, RIGHT_PAREN, RIGHT_CURLY);

        //{a:${a:a}}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP, DOLLAR, LEFT_CURLY,
                WORD, PARAM_EXPANSION_OP, WORD, RIGHT_CURLY, RIGHT_CURLY);

        //{a:"a"}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP, STRING_BEGIN, WORD, STRING_END, RIGHT_CURLY);
    }
}
