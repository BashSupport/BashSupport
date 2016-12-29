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
import org.junit.Test;

public class SubshellParsingFunctionTest extends MockPsiTest {
    MockFunction subshellCommand = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.subshellParser.parse(psi);
        }
    };


    @Test
    public void testParse() throws Exception {
        //$()
        mockTest(subshellCommand, LEFT_PAREN, RIGHT_PAREN);
    }

    @Test
    public void testIssue341(){
        // $(cd "`dirname "$0"`")
        mockTest(subshellCommand, LEFT_PAREN, WORD, WHITESPACE, STRING_BEGIN, BACKQUOTE, WORD, WHITESPACE, STRING_BEGIN, VARIABLE, STRING_END, BACKQUOTE, STRING_END, RIGHT_PAREN);
    }

    @Test
    public void testIssue367() throws Exception {
        //[[ $(< $1) ]]
        mockTest(subshellCommand, LEFT_PAREN, LESS_THAN, WHITESPACE, VARIABLE, RIGHT_PAREN);
    }
}