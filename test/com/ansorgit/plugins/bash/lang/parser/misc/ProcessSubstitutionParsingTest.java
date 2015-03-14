/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ProcessSubstitutionParsingTest.java, Class: ProcessSubstitutionParsingTest
 * Last modified: 2010-07-10
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
 * Date: 10.07.2010
 * Time: 15:37:48
 */
public class ProcessSubstitutionParsingTest extends MockPsiTest {
    MockFunction parsingFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.processSubstitutionParsing.parse(psi);
        }
    };

    @Test
    public void testParse() throws Exception {
        mockTest(parsingFunction, LESS_THAN, LEFT_PAREN, WORD, RIGHT_PAREN);
        mockTest(parsingFunction, LESS_THAN, LEFT_PAREN, WORD, AND_AND, WORD, RIGHT_PAREN);
        mockTest(parsingFunction, LESS_THAN, LEFT_PAREN, WORD, AND_AND, WORD, VARIABLE, RIGHT_PAREN);
    }

    @Test
    public void testParseError() {
        mockTestFail(parsingFunction, LESS_THAN, WHITESPACE, LEFT_PAREN, WORD, RIGHT_PAREN);
    }
}
