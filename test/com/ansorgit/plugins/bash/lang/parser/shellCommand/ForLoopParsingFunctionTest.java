/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ForLoopParsingFunctionTest.java, Class: ForLoopParsingFunctionTest
 * Last modified: 2010-06-05
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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Collections;

/**
 * @author jansorg
 */
public class ForLoopParsingFunctionTest extends MockPsiTest {
    MockFunction forLoop = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.forLoopParser.parse(psi);
        }
    };

    @Test
    public void testForLoopCompoundBlock() {
        //for a in; do echo; done
        mockTest(forLoop,
                Lists.newArrayList("for", "a", "in"),
                FOR_KEYWORD, WORD, WORD, SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD);

        //for f in 1; do {
        //echo 1
        //} done
        mockTest(forLoop,
                Lists.newArrayList("for", "a", "in"),
                FOR_KEYWORD, WORD, WORD, INTEGER_LITERAL, SEMI, DO_KEYWORD, LEFT_CURLY, LINE_FEED,
                WORD, WHITESPACE, WORD, LINE_FEED, RIGHT_CURLY, WHITESPACE, DONE_KEYWORD);

        //for f in 1; do {
        //echo 1
        //}
        // done
        mockTest(forLoop,
                Lists.newArrayList("for", "a", "in"),
                FOR_KEYWORD, WORD, WORD, INTEGER_LITERAL, SEMI, DO_KEYWORD, LEFT_CURLY, LINE_FEED,
                WORD, WHITESPACE, WORD, LINE_FEED, RIGHT_CURLY, WHITESPACE, LINE_FEED, DONE_KEYWORD);

        //for f in 1; do {
        //echo 1
        //};
        // done
        mockTest(forLoop,
                Lists.newArrayList("for", "a", "in"),
                FOR_KEYWORD, WORD, WORD, INTEGER_LITERAL, SEMI, DO_KEYWORD, LEFT_CURLY, LINE_FEED,
                WORD, WHITESPACE, WORD, LINE_FEED, RIGHT_CURLY, SEMI, LINE_FEED, DONE_KEYWORD);

        //for A do echo $A; done
        mockTest(forLoop, FOR_KEYWORD, WORD, DO_KEYWORD, WHITESPACE, WORD, VARIABLE, SEMI, DONE_KEYWORD);
    }

    @Test
    public void testErrors() throws Exception {
        //for a in; do echo done
        mockTestError(BashVersion.Bash_v3, forLoop,
                Lists.newArrayList("for", "a", "in"),
                FOR_KEYWORD, WORD, WORD, SEMI, DO_KEYWORD, WORD, DONE_KEYWORD);

    }

    @Test
    public void testIncompleteParse() throws Exception {
        //error markers must be present, but the incomplete if should be parsed without remaining elements

        // for f in a; do; done
        mockTestError(BashVersion.Bash_v3, forLoop, false, true,
                Lists.newArrayList("for", "a", "in"),
                FOR_KEYWORD, WORD, WORD, WORD, SEMI, DO_KEYWORD, SEMI, DONE_KEYWORD);
    }
}
