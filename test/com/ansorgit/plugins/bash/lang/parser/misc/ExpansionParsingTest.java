/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ExpansionParsingTest.java, Class: ExpansionParsingTest
 * Last modified: 2010-01-27
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

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

/**
 * User: jansorg
 * Date: Nov 15, 2009
 * Time: 12:09:48 AM
 */
public class ExpansionParsingTest extends MockPsiTest {
    private MockFunction parser = new MockFunction() {
        @Override
        public boolean preCheck(BashPsiBuilder psi) {
            return Parsing.braceExpansionParsing.isValid(psi);
        }

        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.braceExpansionParsing.parse(psi);
        }
    };

    @Test
    public void testParse() throws Exception {
        //{}
        mockTest(parser, LEFT_CURLY, RIGHT_CURLY);
        //{a}
        mockTest(parser, LEFT_CURLY, WORD, RIGHT_CURLY);
        //{a}
        mockTest(parser, WORD, LEFT_CURLY, WORD, RIGHT_CURLY);
        //{1}
        mockTest(parser, LEFT_CURLY, INTEGER_LITERAL, RIGHT_CURLY);
        //{1,a}
        mockTest(parser, LEFT_CURLY, INTEGER_LITERAL, COMMA, WORD, RIGHT_CURLY);
        //{1,a,2}z
        mockTest(parser, LEFT_CURLY, INTEGER_LITERAL, COMMA, WORD, COMMA, INTEGER_LITERAL, RIGHT_CURLY, WORD);
    }

    @Test
    public void testParseInvalid() {
        //{a..}
        //mockTestFail(parser, LEFT_CURLY, WORD, WORD, WORD, RIGHT_CURLY);
        //{ a}
        mockTestFail(parser, LEFT_CURLY, WHITESPACE, WORD, RIGHT_CURLY);
        //{a }
        mockTestFail(parser, LEFT_CURLY, WORD, WHITESPACE, RIGHT_CURLY);
        //{ }
        mockTestFail(parser, LEFT_CURLY, WHITESPACE, RIGHT_CURLY);
        //a
        mockTestFail(parser, WORD);
    }
}
