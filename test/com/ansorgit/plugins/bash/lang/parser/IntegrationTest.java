/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: IntegrationTest.java, Class: IntegrationTest
 * Last modified: 2010-04-23
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

/**
 * Date: 26.03.2009
 * Time: 13:04:49
 *
 * @author Joachim Ansorg
 */
public class IntegrationTest extends MockPsiTest {
    private static final MockFunction fileParsingTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.file.parseFile(builder);
        }
    };

    @Test
    public void testIntegration1() {
        /*
        if [ -e /lib/init/splash-functions-base ] ; then
            . /lib/init/splash-functions-base
        else
            # Quiet down script if old initscripts version without /lib/init/splash-functions-base is used.
            splash_progress() { return 1; }
        fi
         */

        mockTest(fileParsingTest,
                IF_KEYWORD, EXPR_CONDITIONAL, COND_OP, WORD, _EXPR_CONDITIONAL, SEMI, THEN_KEYWORD, LINE_FEED,
                INTERNAL_COMMAND, WORD, LINE_FEED,
                ELSE_KEYWORD, LINE_FEED,
                WORD, LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, WHITESPACE, WORD, WORD, SEMI, RIGHT_CURLY, LINE_FEED,
                FI_KEYWORD
        );
    }

    @Test
    public void testIntegration2() {
        /*
        if [ startpar = "$CONCURRENCY" ] ; then
            test -s /etc/init.d/.depend.boot  || CONCURRENCY="none"
            test -s /etc/init.d/.depend.start || CONCURRENCY="none"
        fi
         */

        mockTest(fileParsingTest,
                IF_KEYWORD, EXPR_CONDITIONAL, WORD, COND_OP, WORD, _EXPR_CONDITIONAL, SEMI, THEN_KEYWORD, LINE_FEED,
                WORD, WORD, WORD, OR_OR, ASSIGNMENT_WORD, EQ, WORD, LINE_FEED,
                WORD, WORD, WORD, OR_OR, ASSIGNMENT_WORD, EQ, WORD, LINE_FEED,
                FI_KEYWORD
        );
    }

    @Test
    public void testIntegration3() {
        /*
        case "$script" in
          *.sh)
            ;;
          *)
            $debug "$script" $action &
            startup_progress
            backgrounded=1
            ;;
        esac
         */

        mockTest(fileParsingTest,
                CASE_KEYWORD, STRING_BEGIN, WORD, STRING_END, IN_KEYWORD, LINE_FEED,
                WORD, RIGHT_PAREN, LINE_FEED,
                CASE_END, LINE_FEED,
                WORD, RIGHT_PAREN, LINE_FEED,
                VARIABLE, STRING_BEGIN, WORD, STRING_END, VARIABLE, AMP, LINE_FEED,
                WORD, LINE_FEED,
                ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, LINE_FEED,
                CASE_END, LINE_FEED,
                ESAC_KEYWORD
        );
    }

    @Test
    public void testIntegration4() {
        /*
        case "$CONCURRENCY" in
          shell)
            startup() {
                [ 1 = "$backgrounded" ]
            }
            ;;
        esac
         */

        mockTest(fileParsingTest,
                CASE_KEYWORD, STRING_BEGIN, WORD, STRING_END, IN_KEYWORD, LINE_FEED,
                WORD, RIGHT_PAREN, LINE_FEED,
                WORD, LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, LINE_FEED,
                EXPR_CONDITIONAL, INTEGER_LITERAL, COND_OP, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL, LINE_FEED,
                RIGHT_CURLY, LINE_FEED,
                CASE_END, LINE_FEED,
                ESAC_KEYWORD
        );

    }

    @Test
    public void testIntegration5() {
        /*
        case a in
          a)
            startup() {
                [ -z "" ] && echo;
            } ;;
        esac
         */

        mockTest(fileParsingTest,
                CASE_KEYWORD, WORD, IN_KEYWORD, LINE_FEED, WORD, RIGHT_PAREN, LINE_FEED,
                WORD, LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, LINE_FEED,
                EXPR_CONDITIONAL, COND_OP, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL, AND_AND, WORD, SEMI, LINE_FEED,
                RIGHT_CURLY, CASE_END, LINE_FEED,
                ESAC_KEYWORD
        );

    }

    @Test
    public void testIntegration6() {
        /*
        : > a
         */

        mockTest(fileParsingTest, COLON, GREATER_THAN, WORD);
    }

    @Test
    public void testIntegration7() {
        /*
        a < a > a
         */

        mockTest(fileParsingTest, WORD, LESS_THAN, WORD, GREATER_THAN, WORD);
    }

    @Test
    public void testIntegration8() {
        /*
            $action < ${DEVICE_PREFIX}
         */
        mockTest(fileParsingTest, VARIABLE, LESS_THAN, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);

        //${#a}
        mockTest(fileParsingTest, DOLLAR, LEFT_CURLY, WORD, WORD, RIGHT_CURLY);

        //$ {#a}
        mockTestFail(fileParsingTest, DOLLAR, WHITESPACE, LEFT_CURLY, WORD, WORD, RIGHT_CURLY);
    }

    @Test
    public void testIntegration9() {
        /*
            $(($(echo "$1")))
         */

        mockTest(fileParsingTest, EXPR_ARITH, DOLLAR, LEFT_PAREN, WORD, STRING_BEGIN, WORD, STRING_END, RIGHT_PAREN, _EXPR_ARITH);
    }

    @Test
    public void testIntegration10() {
        //echo `echo a` `echo a`
        mockTest(fileParsingTest,
                WORD, BACKQUOTE, WORD, WORD, BACKQUOTE, BACKQUOTE, WORD, WORD, BACKQUOTE);

        //`echo `echo a` `echo b``
        mockTest(fileParsingTest,
                BACKQUOTE,
                WORD, BACKQUOTE, WORD, WORD, BACKQUOTE,
                BACKQUOTE, WORD, WORD, BACKQUOTE,
                BACKQUOTE);
    }

    @Test
    public void testIntegration11() {
        //This is an echo command which has four params: empty backquote, echo, a, empty backquote
        //echo `` echo a ``
        mockTest(fileParsingTest,
                WORD, BACKQUOTE, BACKQUOTE, WORD, WORD, BACKQUOTE, BACKQUOTE);
    }

    @Test
    public void testIntegration12() {
        //Invalid code:
        //function a for f in 1; do echo; done;
        mockTestError(fileParsingTest,
                FUNCTION_KEYWORD, WORD, FOR_KEYWORD, WORD, IN_KEYWORD, WORD,
                SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD, SEMI);

        //function a { for f in 1; do echo; done; }
        mockTest(fileParsingTest,
                FUNCTION_KEYWORD, WORD, LEFT_CURLY, WHITESPACE, FOR_KEYWORD, WORD, IN_KEYWORD, WORD,
                SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD, SEMI, RIGHT_CURLY);

        //f() { export a=1 b=2; }
        mockTest(fileParsingTest, Lists.newArrayList("f", "(", ")", "{", " ", "export"),
                WORD, LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, WHITESPACE,
                INTERNAL_COMMAND, ASSIGNMENT_WORD, EQ, WORD, WHITESPACE, WORD, EQ, WORD,
                SEMI, WHITESPACE, RIGHT_CURLY);
    }

    @Test
    public void testIntegration13() {
        // echo
        // ((a=1))
        mockTest(fileParsingTest,
                WORD, LINE_FEED, EXPR_ARITH, ASSIGNMENT_WORD, EQ, NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testIntegration14() {
        /*
          if a; then
            if a; then
                echo 1
            else
                echo 0
            fi > a
          fi
         */
        mockTest(fileParsingTest,
                IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, LINE_FEED,
                IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, LINE_FEED,
                WORD, WORD, LINE_FEED,
                ELSE_KEYWORD, LINE_FEED,
                WORD, WORD, LINE_FEED,
                FI_KEYWORD, GREATER_THAN, WORD, LINE_FEED,
                FI_KEYWORD);
    }

    @Test
    public void testIntegration15() {
        /*
        for index in 1 2
        do
          echo
        done
         */
        mockTest(fileParsingTest,
                FOR_KEYWORD, WORD, IN_KEYWORD, INTEGER_LITERAL, INTEGER_LITERAL, LINE_FEED,
                DO_KEYWORD, LINE_FEED, INTERNAL_COMMAND, LINE_FEED, DONE_KEYWORD
        );

        /*
        for index in 1 2;
        do
          echo
        done
         */
        mockTest(fileParsingTest,
                FOR_KEYWORD, WORD, IN_KEYWORD, INTEGER_LITERAL, INTEGER_LITERAL, SEMI, LINE_FEED,
                DO_KEYWORD, LINE_FEED, INTERNAL_COMMAND, LINE_FEED, DONE_KEYWORD
        );
    }

    @Test
    public void testIntegration16() {
        //"case a in a) echo [ \"a\" ];; esac"
        mockTest(fileParsingTest,
                CASE_KEYWORD, WORD, IN_KEYWORD, WORD,
                RIGHT_PAREN, INTERNAL_COMMAND, EXPR_CONDITIONAL, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL, CASE_END,
                ESAC_KEYWORD);
    }

    @Test
    public void testPipelineWithConditional() {
        //echo && [ -z "hi" ]
        mockTest(fileParsingTest, INTERNAL_COMMAND, AND_AND, EXPR_CONDITIONAL, COND_OP, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL);

        //for f in a; do echo && [ -z "hi" ]; done
        mockTest(fileParsingTest,
                FOR_KEYWORD, WORD, IN_KEYWORD, WORD, SEMI, DO_KEYWORD,
                INTERNAL_COMMAND, AND_AND, EXPR_CONDITIONAL, COND_OP, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL,
                SEMI, DONE_KEYWORD);
    }

    @Test
    public void testWhileErrorConstruct() {
        //function a { while a; do ; done }
        mockTestError(fileParsingTest,
                FUNCTION_KEYWORD, WORD, LEFT_CURLY, WHILE_KEYWORD, WORD, SEMI, DO_KEYWORD, SEMI, DONE_KEYWORD, RIGHT_CURLY);

        //function a { while ; do a; done }
        mockTestError(fileParsingTest,
                FUNCTION_KEYWORD, WORD, LEFT_CURLY, WHILE_KEYWORD, SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD, RIGHT_CURLY);
    }

    @Test
    public void testIfErrorConstruct() {
        //function a { if a; then ; done }
        mockTestError(fileParsingTest,
                FUNCTION_KEYWORD, WORD, LEFT_CURLY,
                IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, SEMI, DONE_KEYWORD,
                RIGHT_CURLY);
    }

    @Test
    public void testBuildInCommandDeclare() {
        //declare $abc=()
        mockTest(fileParsingTest, Lists.newArrayList("declare"), INTERNAL_COMMAND, VARIABLE, EQ, LEFT_PAREN, RIGHT_PAREN);
        //declare $abc=(a)
        mockTest(fileParsingTest, Lists.newArrayList("declare"), INTERNAL_COMMAND, VARIABLE, EQ, LEFT_PAREN, WORD, RIGHT_PAREN);
        //declare $abc=(a,b)
        mockTest(fileParsingTest, Lists.newArrayList("declare"), INTERNAL_COMMAND, VARIABLE, EQ, LEFT_PAREN, WORD, WORD, RIGHT_PAREN);
    }

    @Test
    public void testAssigmentCommands() {
        //echo a=a=b
        mockTest(fileParsingTest, INTERNAL_COMMAND, ASSIGNMENT_WORD, EQ, ASSIGNMENT_WORD, WORD);
        //a=a=b
        mockTest(fileParsingTest, ASSIGNMENT_WORD, EQ, ASSIGNMENT_WORD, WORD);
        //a=b=c=d
        mockTest(fileParsingTest, ASSIGNMENT_WORD, EQ, ASSIGNMENT_WORD, EQ, ASSIGNMENT_WORD, EQ, WORD);
    }

    @Test
    public void testConditionalWithBackquote() {
        //[ `uname -s` = "SunOS" ]
        mockTest(fileParsingTest, EXPR_CONDITIONAL, BACKQUOTE, INTERNAL_COMMAND, WORD, BACKQUOTE, COND_OP, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL);
        //[ $(uname -s) = "SunOS" ]
        mockTest(fileParsingTest, EXPR_CONDITIONAL, DOLLAR, LEFT_PAREN, INTERNAL_COMMAND, WORD, RIGHT_PAREN, COND_OP, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL);
    }

    @Test
    public void testNewlinesAfterCommand() {
        //a
        //#Hey
        //

        mockTest(fileParsingTest, WORD, LINE_FEED, COMMENT, LINE_FEED);
    }

    @Test
    public void testNestedStatements() {
        //if a; then PIDDIR=a$(a) a; fi
        mockTest(fileParsingTest, IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, ASSIGNMENT_WORD, EQ, WORD, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, WHITESPACE, WORD, SEMI, FI_KEYWORD);
        //case $(a) in a) ;; esac
        mockTest(fileParsingTest,
                CASE_KEYWORD, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, WHITESPACE, IN_KEYWORD,
                WORD, RIGHT_PAREN, CASE_END, ESAC_KEYWORD);
        //if a; then
        //   b #end
        //fi
        mockTest(fileParsingTest,
                IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, LINE_FEED, WORD, WHITESPACE, COMMENT, LINE_FEED,
                FI_KEYWORD);
    }

    @Test
    public void testParseSimpleCommandCombinedError2() { //error case
        mockTestError(fileParsingTest, PIPE, WORD); // | b
    }


    @Test
    public void testAppendAssignment() {
        //a+=a
        mockTest(fileParsingTest, ASSIGNMENT_WORD, ADD_EQ, WORD);
    }

    @Test
    public void testArithmeticForLoopVarError() {
        //for (( a *2 < 4; 3 >= a; a = a*2 )) ; do hey $a; done;
        //$a is here written as VAR, WORD which is an invalid combination
        //this has to be detected as an error
        mockTestError(fileParsingTest,
                FOR_KEYWORD, EXPR_ARITH, WORD, ARITH_MULT, NUMBER, ARITH_LT, NUMBER, SEMI,
                NUMBER, ARITH_GE, WORD, SEMI,
                WORD, EQ, WORD, ARITH_MULT, NUMBER, _EXPR_ARITH,
                DO_KEYWORD, WORD, DOLLAR, WORD, SEMI, DONE_KEYWORD, SEMI);
    }

    @Test
    public void testArithmeticForLoop() {
        //for (( a *2 < 4; 3 >= a; a = a*2 )) ; do hey $a; done;
        mockTest(fileParsingTest,
                FOR_KEYWORD, EXPR_ARITH, WORD, ARITH_MULT, NUMBER, ARITH_LT, NUMBER, SEMI,
                NUMBER, ARITH_GE, WORD, SEMI,
                WORD, EQ, WORD, ARITH_MULT, NUMBER, _EXPR_ARITH,
                DO_KEYWORD, WORD, VARIABLE, SEMI, DONE_KEYWORD, SEMI);

        //for (( ; ; )) ; do hey $a; done;
        mockTest(fileParsingTest,
                FOR_KEYWORD, EXPR_ARITH, SEMI, SEMI, _EXPR_ARITH,
                DO_KEYWORD, WORD, VARIABLE, SEMI, DONE_KEYWORD, SEMI);

        //for (( a ; b; c )) ; do hey $a; done;
        mockTest(fileParsingTest,
                FOR_KEYWORD, EXPR_ARITH, WORD, SEMI, WORD, SEMI, _EXPR_ARITH,
                DO_KEYWORD, WORD, VARIABLE, SEMI, DONE_KEYWORD, SEMI);
        testArithmeticVarAssignment();


    }

    @Test
    public void testArithmeticVarAssignment() {
        //for (( c=1; c<=5; c++ ))
        //do
        //    echo "Welcome $c times..."
        //done
        mockTest(fileParsingTest,
                FOR_KEYWORD, EXPR_ARITH, ASSIGNMENT_WORD, EQ, NUMBER, SEMI, WORD, ARITH_LE, NUMBER, SEMI, WORD, ARITH_PLUS_PLUS, _EXPR_ARITH,
                LINE_FEED, DO_KEYWORD, LINE_FEED, WORD, WORD, LINE_FEED, DONE_KEYWORD, SEMI);
    }

    @Test
    public void testShebang() {
        //#!/bin/bash
        mockTest(fileParsingTest, SHEBANG);

        //\n\n#!/bin/sh
        mockTest(fileParsingTest, LINE_FEED, LINE_FEED, SHEBANG);

        //a #!/bin/sh
        mockTest(fileParsingTest, Lists.newArrayList("a", "#!/bin/sh"), WORD, COMMENT);

        //echo a; #!/bin/sh
        mockTest(fileParsingTest, INTERNAL_COMMAND, WORD, SEMI, COMMENT);
    }

    @Test
    public void testIfWithReadRedirect() {
        //	if read pid > c; then
        //      a
        //	fi
        mockTest(fileParsingTest, Lists.newArrayList("if", "read", "pid"),
                IF_KEYWORD, INTERNAL_COMMAND, WORD, GREATER_THAN, WORD, SEMI, THEN_KEYWORD, LINE_FEED,
                WORD, LINE_FEED,
                FI_KEYWORD);
    }

    @Test
    public void testIfWithFaultyRedirect() {
        //	if read pid > c; then
        //      a
        //	fi
        mockTestError(fileParsingTest,
                IF_KEYWORD, INTERNAL_COMMAND, WORD, GREATER_THAN, SEMI, THEN_KEYWORD, LINE_FEED,
                WORD, LINE_FEED,
                FI_KEYWORD);
    }

    @Test
    public void testIfWithNormalRedirect() {
        //	if echo pid > c; then
        //      a
        //	fi
        mockTest(fileParsingTest, Lists.newArrayList("if", "echo", "pid"),
                IF_KEYWORD, INTERNAL_COMMAND, WORD, GREATER_THAN, WORD, SEMI, THEN_KEYWORD, LINE_FEED,
                WORD, LINE_FEED,
                FI_KEYWORD);
    }

    @Test
    public void testIfWithLocalVarRedirect() {
        //	if echo pid > c; then
        //      a
        //	fi
        mockTest(fileParsingTest, Lists.newArrayList("if", "echo", "pid"),
                IF_KEYWORD, INTERNAL_COMMAND, WORD, GREATER_THAN, WORD, SEMI, THEN_KEYWORD, LINE_FEED,
                WORD, LINE_FEED,
                FI_KEYWORD);
    }

    @Test
    public void testFunctionWithReadArgs() {
        //make sure that the -p option is not taken as a variable name
        //$(read -p "")
        mockTest(fileParsingTest, Lists.newArrayList("$", "(", "read", " ", "-p", " "),
                DOLLAR, LEFT_PAREN, INTERNAL_COMMAND, WHITESPACE, WORD, WHITESPACE, STRING_BEGIN, WORD,
                STRING_END, RIGHT_PAREN);
    }

    @Test
    public void testHeredocInLoop() {
        //for f in 1; do echo <<EOF
        //  heredoccontent
        //EOF
        //done
        mockTest(fileParsingTest,
                Lists.newArrayList("for", "f", "in", "1", ";", "do", "echo", "<<", "EOF",
                        "\n", "heredoccontent", "(", "\n", "EOF", "\n", "done"),
                FOR_KEYWORD, WORD, IN_KEYWORD, INTEGER_LITERAL, SEMI,
                DO_KEYWORD, WORD, REDIRECT_LESS_LESS, WORD, LINE_FEED, WORD, LEFT_PAREN, LINE_FEED, WORD, LINE_FEED,
                DONE_KEYWORD);


    }

    @Test
    public void testInvalidHereDoc() {
        //for f in 1; do echo <<EOF
        //  heredoccontent
        //      EOF
        //done
        mockTestError(BashVersion.Bash_v3, fileParsingTest, false,
                Lists.newArrayList("for", "f", "in", "1", ";", "do", "echo", "<<", "EOF", "\n", "heredoccontent", "(", "\n", "   ", "EOF", "\n", "done"),
                FOR_KEYWORD, WORD, IN_KEYWORD, INTEGER_LITERAL, SEMI,
                DO_KEYWORD, WORD, REDIRECT_LESS_LESS, WORD, LINE_FEED, WORD, LEFT_PAREN, LINE_FEED, WHITESPACE, WORD, LINE_FEED,
                DONE_KEYWORD);
    }

    @Test
    public void testBashV4() {
        mockTest(BashVersion.Bash_v4, fileParsingTest, WORD, PIPE_AMP, WORD);
    }


    @Test
    public void testFunction() {
        //doIt() {
        //export PATH= "a"
        //}
        mockTest(fileParsingTest, Lists.newArrayList("doIt", "(", ")", "{", "\n", "export"),
                WORD, LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, LINE_FEED,
                INTERNAL_COMMAND, ASSIGNMENT_WORD, EQ, WHITESPACE, STRING_BEGIN, WORD, STRING_END, LINE_FEED,
                RIGHT_CURLY);
    }

    @Test
    public void testFunction2() {
        //a() {
        //    echo in
        //}
        mockTest(fileParsingTest, Lists.newArrayList("a", "(", ")", "{", "\n", "echo", "in"),
                WORD, LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, LINE_FEED, INTERNAL_COMMAND, IN_KEYWORD, LINE_FEED,
                RIGHT_CURLY);
    }

    @Test
    public void testComplicatedHereDoc() {
        //a <<-"END"
        // "TEST
        //END
        mockTest(fileParsingTest,
                Lists.newArrayList("a", "<<", "END", "\n", "\"", "TEST", "\n", "END"),
                WORD, REDIRECT_LESS_LESS, WORD, LINE_FEED, STRING_BEGIN, WORD, LINE_FEED, WORD);
    }

    @Test
    public void testRedirects() {
        //> OUT
        mockTest(fileParsingTest, WHITESPACE, GREATER_THAN, WORD);
        //: > OUT
        mockTest(fileParsingTest, COLON, WHITESPACE, GREATER_THAN, WORD);
        //&> OUT
        mockTest(BashVersion.Bash_v4, fileParsingTest, REDIRECT_AMP_GREATER, WORD);

        //exec 9 <& 0 < /etc/fstab
        mockTest(fileParsingTest, WORD, INTEGER_LITERAL, WHITESPACE, REDIRECT_LESS_AMP, WHITESPACE, INTEGER_LITERAL,
                LESS_THAN, WHITESPACE, WORD);
    }
}