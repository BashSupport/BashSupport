/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: LetCommandTest.java, Class: LetCommandTest
 * Last modified: 2013-04-30
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

package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * @author jansorg
 */
public class LetCommandTest extends MockPsiTest {
    MockFunction parserFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return new LetCommand().parse(psi);
        }
    };


    @Test
    public void testParse() {
        //let a=1
        mockTest(parserFunction, Lists.newArrayList("let"), LET_KEYWORD, WHITESPACE, ASSIGNMENT_WORD, EQ, ARITH_NUMBER);

        //let a=1+1
        mockTest(parserFunction, Lists.newArrayList("let"), LET_KEYWORD, WHITESPACE, ASSIGNMENT_WORD, EQ, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER);

        //let a+=1
        mockTest(parserFunction, Lists.newArrayList("let"), LET_KEYWORD, WHITESPACE, ASSIGNMENT_WORD, ARITH_ASS_PLUS, ARITH_NUMBER);

        //let a=1 b=1
//        mockTest(parserFunction, Lists.newArrayList("let"), WORD, WHITESPACE, ASSIGNMENT_WORD, EQ, NUMBER, WHITESPACE, ASSIGNMENT_WORD, EQ, NUMBER);

        //let "a=1"
        //mockTest(parserFunction, Lists.newArrayList("let"), WORD, WHITESPACE, STRING_BEGIN, ASSIGNMENT_WORD, EQ, NUMBER, STRING_END);
    }
}
