/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: VarParsingTest.java, Class: VarParsingTest
 * Last modified: 2010-02-20
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
 * Date: 27.03.2009
 * Time: 11:15:50
 *
 * @author Joachim Ansorg
 */
public class VarParsingTest extends MockPsiTest {
    private MockFunction varParsingTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.var.parse(builder);
        }
    };

    @Test
    public void testParse1() {
        mockTest(varParsingTest, VARIABLE);
    }

    @Test
    public void testParse2() {
        //$(echo a)
        mockTest(varParsingTest, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN);
    }

    @Test
    public void testParse3() {
        //${a}
        mockTest(varParsingTest, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        //${1}
        mockTest(varParsingTest, DOLLAR, LEFT_CURLY, INTEGER_LITERAL, RIGHT_CURLY);
    }

    @Test
    public void testParse4() {
        //$((1))
        mockTest(varParsingTest, DOLLAR, EXPR_ARITH, NUMBER, _EXPR_ARITH);
    }

/*    @Test
    public void testParseError1() {
        //${a;}
        mockTestFail(varParsingTest, DOLLAR, LEFT_CURLY, WORD, SEMI, RIGHT_CURLY);
    }*/

    @Test
    public void testParseError2() {
        //${a a}
        //mockTestError(varParsingTest, DOLLAR, LEFT_CURLY, WORD, WORD, RIGHT_CURLY);
    }


}
