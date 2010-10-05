/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ShellCommandParsingTest.java, Class: ShellCommandParsingTest
 * Last modified: 2010-10-05
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

import junit.framework.Assert;
import org.junit.Test;

import static com.ansorgit.plugins.bash.lang.BashVersion.Bash_v3;

/**
 * Date: 25.03.2009
 * Time: 14:07:10
 *
 * @author Joachim Ansorg
 */
public class ShellCommandParsingTest extends MockPsiTest {
    private final MockFunction arithmeticParsingTester = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.shellCommand.arithmeticParser.parse(builder);
        }
    };

    private final MockFunction subshellParsingTester = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.shellCommand.subshellParser.parse(builder);
        }
    };

    private final MockFunction ifCommandParsingTester = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.shellCommand.ifParser.parse(builder);
        }
    };

    private final MockFunction backquoteCommandParsingTester = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.shellCommand.backtickParser.parse(builder);
        }
    };

    private final MockFunction commandGroupParsingTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.shellCommand.groupCommandParser.parse(builder);
        }
    };

    private final MockFunction wordParserTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.word.parseWord(builder);
        }
    };

    @Test
    public void testIfCommand1() {
        mockTest(ifCommandParsingTester, IF_KEYWORD, WORD, SEMI,
                THEN_KEYWORD, WORD, WORD, SEMI, FI_KEYWORD); //if a; then b c; fi
    }

    @Test
    public void testIfCommand2() {
        mockTest(ifCommandParsingTester, IF_KEYWORD, WORD, SEMI,
                THEN_KEYWORD, WORD, WORD, SEMI, ELSE_KEYWORD, WORD, SEMI, FI_KEYWORD); //if a; then b c; else d; fi
    }

    @Test
    public void testIfCommand3() {
        //if a; then b c; elif d; then e &; elif a; then b; else f; fi
        mockTest(ifCommandParsingTester, IF_KEYWORD, WORD, SEMI,
                THEN_KEYWORD, WORD, WORD, SEMI,
                ELIF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, SEMI,
                ELIF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, SEMI,
                ELSE_KEYWORD, WORD, SEMI, FI_KEYWORD);
    }

    @Test
    public void testIfCommand4() {
        //if a
        // then b c
        // elif d
        // then e &
        // elif a
        // then b
        // else f
        //  fi
        mockTest(ifCommandParsingTester, IF_KEYWORD, WORD, LINE_FEED,
                THEN_KEYWORD, WORD, WORD, LINE_FEED,
                ELIF_KEYWORD, WORD, LINE_FEED, THEN_KEYWORD, WORD, LINE_FEED,
                ELIF_KEYWORD, WORD, LINE_FEED, THEN_KEYWORD, WORD, LINE_FEED,
                ELSE_KEYWORD, WORD, LINE_FEED, FI_KEYWORD);
    }

    @Test
    public void testIfCommandError1() {
        //code with errors

        //if a; then b c; elif d; then e &;else c; elif a; then b; else f; fi
        mockTestError(ifCommandParsingTester, IF_KEYWORD, WORD, SEMI,
                THEN_KEYWORD, WORD, WORD, SEMI,
                ELIF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, SEMI,
                ELSE_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, SEMI,
                ELIF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, SEMI,
                ELSE_KEYWORD, WORD, SEMI, FI_KEYWORD);
    }

    @Test
    public void testIfCommandError2() {
        //if a then b; fi
        mockTestError(ifCommandParsingTester,
                IF_KEYWORD, WORD, THEN_KEYWORD, WORD, SEMI, FI_KEYWORD);
    }

    @Test
    public void testSubshellCommand1() {
        //(echo a &)
        mockTest(subshellParsingTester,
                LEFT_PAREN, WORD, WORD, AMP, RIGHT_PAREN);
    }

    @Test
    public void testSubshellCommand2() {
        //(echo a)
        mockTest(subshellParsingTester,
                LEFT_PAREN, WORD, WORD, RIGHT_PAREN
        );
    }

    @Test
    public void testSubshellCommand3() {
        //(echo a)
        mockTest(subshellParsingTester,
                LEFT_PAREN, WORD, WORD, RIGHT_PAREN
        );
    }

    @Test
    public void testSubshellCommandError1() {
        //()
        mockTestError(subshellParsingTester, LEFT_PAREN, RIGHT_PAREN);
    }

    @Test
    public void testIsBackquoteCommand() {
        //`a`
        final MockPsiBuilder mockBuilder = new MockPsiBuilder(BACKQUOTE, WORD, BACKQUOTE);
        BashPsiBuilder b = new BashPsiBuilder(null, mockBuilder, Bash_v3);
        Assert.assertTrue(Parsing.shellCommand.backtickParser.isValid(b));

        //`echo` echo a ``
        b = new BashPsiBuilder(null, new MockPsiBuilder(BACKQUOTE, WORD, BACKQUOTE, WORD, WORD, BACKQUOTE, BACKQUOTE), Bash_v3);
        Assert.assertTrue(Parsing.shellCommand.backtickParser.isValid(b));

        //invalid: ``echo a`
        //b = new BashPsiBuilder(new MockPsiBuilder(BACKQUOTE, BACKQUOTE, WORD, BACKQUOTE));
        //Assert.assertFalse(Parsing.shellCommand.isBackquoteCommand(b));

        //`echo` `echo a`
        b = new BashPsiBuilder(null, new MockPsiBuilder(BACKQUOTE, WORD, BACKQUOTE, BACKQUOTE, WORD, BACKQUOTE), Bash_v3);
        Assert.assertTrue(Parsing.shellCommand.backtickParser.isValid(b));


        //``echo a``
        b = new BashPsiBuilder(null, new MockPsiBuilder(BACKQUOTE, BACKQUOTE, WORD, WORD, BACKQUOTE, BACKQUOTE), Bash_v3);
        Assert.assertTrue(Parsing.shellCommand.backtickParser.isValid(b));

        //``
        b = new BashPsiBuilder(null, new MockPsiBuilder(BACKQUOTE, BACKQUOTE), Bash_v3);
        Assert.assertTrue(Parsing.shellCommand.backtickParser.isValid(b));

        //````
        b = new BashPsiBuilder(null, new MockPsiBuilder(BACKQUOTE, BACKQUOTE, BACKQUOTE, BACKQUOTE), Bash_v3);
        Assert.assertTrue(Parsing.shellCommand.backtickParser.isValid(b));

        //``````
        b = new BashPsiBuilder(null, new MockPsiBuilder(BACKQUOTE, BACKQUOTE, BACKQUOTE, BACKQUOTE), Bash_v3);
        Assert.assertTrue(Parsing.shellCommand.backtickParser.isValid(b));

        //`
        b = new BashPsiBuilder(null, new MockPsiBuilder(BACKQUOTE), Bash_v3);
        Assert.assertTrue(Parsing.shellCommand.backtickParser.isValid(b));
    }

    @Test
    public void testBackquoteCommand() {
        //`echo`
        mockTest(backquoteCommandParsingTester,
                BACKQUOTE, WORD, BACKQUOTE);

        //`echo a`
        mockTest(backquoteCommandParsingTester,
                BACKQUOTE, WORD, WORD, BACKQUOTE);

        //`echo`
        mockTest(backquoteCommandParsingTester,
                BACKQUOTE, WORD, BACKQUOTE);

        //``
        mockTest(backquoteCommandParsingTester, BACKQUOTE, BACKQUOTE);

        //`echo $((1))`
        mockTest(backquoteCommandParsingTester,
                BACKQUOTE, WORD, DOLLAR, EXPR_ARITH, NUMBER, _EXPR_ARITH, BACKQUOTE);
    }

    @Test
    public void testBackquoteCommandErrors() {
        //`echo
        mockTestError(backquoteCommandParsingTester,
                BACKQUOTE, WORD);
        //`
        mockTestError(backquoteCommandParsingTester, BACKQUOTE);
    }

    @Test
    public void testParseGroupCommand() {
        //{ echo a;}
        mockTest(commandGroupParsingTest,
                LEFT_CURLY, WHITESPACE, WORD, WORD, SEMI, RIGHT_CURLY);
        //{ echo a; }
        mockTest(commandGroupParsingTest,
                LEFT_CURLY, WHITESPACE, WORD, WORD, SEMI, WHITESPACE, RIGHT_CURLY);
    }

    @Test
    public void testParseGroupCommandError() {
        //{echo a}
        mockTestError(commandGroupParsingTest, LEFT_CURLY, WORD, RIGHT_CURLY);

        //{ echo a}
        mockTestFail(commandGroupParsingTest,
                LEFT_CURLY, WHITESPACE, WORD, WORD, RIGHT_CURLY);
    }

    @Test
    public void testParseArithmeticCommand1() {
        //$((1 + 2))
        mockTest(arithmeticParsingTester,
                EXPR_ARITH, NUMBER, ARITH_PLUS, NUMBER, _EXPR_ARITH
        );
    }

    @Test
    public void testParseArithmeticCommand2() {
        //$((1))
        mockTest(arithmeticParsingTester,
                EXPR_ARITH, NUMBER, _EXPR_ARITH
        );
    }

    @Test
    public void testParseArithmeticCommand3() {
        //$(($a && $a))
        mockTest(arithmeticParsingTester,
                EXPR_ARITH, VARIABLE, AND_AND, VARIABLE, _EXPR_ARITH
        );
    }

    @Test
    public void testParseArithmeticCommand5() {
        //$(((1)))
        mockTest(arithmeticParsingTester, EXPR_ARITH, LEFT_PAREN, NUMBER, RIGHT_PAREN, _EXPR_ARITH);
    }

    @Test
    public void testParseArithmeticCommand6() {
        //$(($(a) + 1))
        mockTest(arithmeticParsingTester,
                EXPR_ARITH,
                DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, ARITH_PLUS, NUMBER,
                _EXPR_ARITH);
    }

    @Test
    public void testParseArithmeticCommand7() {
        //((i=$(echo 1)))
        mockTest(arithmeticParsingTester,
                EXPR_ARITH,
                ASSIGNMENT_WORD, EQ, DOLLAR, LEFT_PAREN, WORD, INTEGER_LITERAL, RIGHT_PAREN,
                _EXPR_ARITH);
    }

    @Test
    public void testParseArithmeticCommand8() {
        //((i=$((1 + 9))))
        mockTest(arithmeticParsingTester,
                EXPR_ARITH,
                ASSIGNMENT_WORD, EQ,
                DOLLAR, EXPR_ARITH, NUMBER, ARITH_PLUS, NUMBER, _EXPR_ARITH,
                _EXPR_ARITH);
    }

    @Test
    public void testParseArithmeticCommandError1() {
        //$(())
        mockTestError(arithmeticParsingTester,
                EXPR_ARITH, _EXPR_ARITH
        );
    }
}
