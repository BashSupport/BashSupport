/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ShellLoopParsingTest.java, Class: ShellLoopParsingTest
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

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Collections;

/**
 * @author jansorg
 */
public class ShellLoopParsingTest extends MockPsiTest {
    private MockFunction forLoopTester = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.shellCommand.forLoopParser.parse(builder);
        }
    };

    private MockFunction whileLoopTester = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.shellCommand.whileParser.parse(builder);
        }
    };

    private MockFunction selectCommandTester = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.shellCommand.selectParser.parse(builder);
        }
    };

    @Test
    public void testWhileLoopSimple() {
        //while a; do b; done
        mockTest(whileLoopTester, WHILE_KEYWORD, WORD, SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD);
    }

    @Test
    public void testWhileLoopError1() {
        //the following code is invalid code, it is parsed completely though

        //while a; do ; done
        mockTestError(BashVersion.Bash_v3, whileLoopTester, false, true, Collections.<String>emptyList(), WHILE_KEYWORD, WORD, SEMI, DO_KEYWORD, SEMI, DONE_KEYWORD);

        //while ; do ; done
        mockTestError(BashVersion.Bash_v3, whileLoopTester, false, true, Collections.<String>emptyList(), WHILE_KEYWORD, SEMI, DO_KEYWORD, SEMI, DONE_KEYWORD);
    }

    @Test
    public void testWhileLoopSimple2() {
        mockTest(whileLoopTester, WHILE_KEYWORD, WORD, LINE_FEED, DO_KEYWORD,
                WORD, LINE_FEED, DONE_KEYWORD); //while a \n do b \n done
    }


    @Test
    public void testForCommand() {
        //for a in a b c; do echo a; done
        mockTest(forLoopTester, Lists.newArrayList("for", "a", "in"),
                FOR_KEYWORD, WORD, WORD, WORD, WORD, WORD, SEMI,
                DO_KEYWORD, WORD, WORD, SEMI, DONE_KEYWORD
        );
    }

    @Test
    public void testForCommand2() {
        //for a;
        // { echo a
        // }
        mockTest(forLoopTester,
                FOR_KEYWORD, WORD, SEMI, LINE_FEED, LEFT_CURLY, WORD, WORD, LINE_FEED, RIGHT_CURLY
        );
    }

    @Test
    public void testForCommand3() {
        //for a;
        // { echo a
        // }
        mockTest(forLoopTester,
                FOR_KEYWORD, WORD, SEMI, LINE_FEED, LEFT_CURLY, WORD, WORD, LINE_FEED, RIGHT_CURLY
        );
    }

    @Test
    public void testForCommand4() {
        //for a in $(a); do echo a; done;
        mockTest(forLoopTester, Lists.newArrayList("for", "a", "in"),
                FOR_KEYWORD, WORD, WORD, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, SEMI, DO_KEYWORD,
                WORD, WORD, SEMI, DONE_KEYWORD
        );
    }

    @Test
    public void testArithForCommand1() {
//        for ((;;)); do e; done
        mockTest(forLoopTester,
                FOR_KEYWORD, EXPR_ARITH, SEMI, SEMI, _EXPR_ARITH, SEMI,
                DO_KEYWORD, WORD, SEMI, DONE_KEYWORD
        );

        // for ((i=1;;)); do e; done
        mockTest(forLoopTester,
                FOR_KEYWORD, EXPR_ARITH, ASSIGNMENT_WORD, EQ, ARITH_NUMBER, SEMI, SEMI, _EXPR_ARITH, SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD
        );

        // for ((i=1;i<10;)); do e; done
        mockTest(forLoopTester,
                FOR_KEYWORD, EXPR_ARITH, ASSIGNMENT_WORD, EQ, ARITH_NUMBER, SEMI, WORD, ARITH_LT, ARITH_NUMBER, SEMI, _EXPR_ARITH, SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD
        );
    }

    @Test
    public void testForCommandError1() {
        //for a;
        // { echo a }
        mockTestError(forLoopTester,
                FOR_KEYWORD, WORD, SEMI, LINE_FEED, LEFT_CURLY, WORD, WORD, RIGHT_CURLY
        );
    }

    @Test
    public void testSelectCommand1() {
        //select a in b; do echo a; done
        mockTest(selectCommandTester, Lists.newArrayList("select", "a", "in"),
                SELECT_KEYWORD, WORD, WORD, WORD, SEMI,
                DO_KEYWORD, WORD, WORD, SEMI, DONE_KEYWORD
        );
    }

    @Test
    public void testSelectCommand2() {
        //select a
        //  do echo a
        //  done
        mockTest(selectCommandTester,
                SELECT_KEYWORD, WORD, LINE_FEED,
                DO_KEYWORD, WORD, WORD, LINE_FEED, DONE_KEYWORD
        );
    }

    @Test
    public void testIssue243() throws Exception {
        //while false; do case "x" in (x);; esac; done;
        mockTest(whileLoopTester,
                Lists.newArrayList("while", "false", ";", "do", "case", "x", "in"),
                WHILE_KEYWORD, WORD, SEMI, DO_KEYWORD, CASE_KEYWORD, STRING2, WORD, LEFT_PAREN, WORD, RIGHT_PAREN, CASE_END, ESAC_KEYWORD, SEMI, DONE_KEYWORD);
    }
}
