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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * @author jansorg
 */
public class FileParsingTest extends MockPsiTest {
    private final MockFunction fileTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.file.parseFile(psi);
        }
    };

    @Test
    public void testFileParsing1() {
        mockTest(fileTest, LINE_FEED);
    }

    @Test
    public void testFileParsing2() {
        mockTest(fileTest, WORD, LINE_FEED);
    }

    @Test
    public void testFileParsing3() {
        mockTest(fileTest, WORD, LINE_FEED, WORD, WORD, AMP); //a \n a b &
    }

    @Test
    public void testFileParsing4() {
        //a \n while a; do b; done;
        mockTest(fileTest, WORD, LINE_FEED, WHILE_KEYWORD,
                WORD, SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD, SEMI);
    }

    @Test
    public void testIssue341() {
        // "`echo "$0"`"
        mockTest(fileTest, STRING_BEGIN, BACKQUOTE, WORD, WHITESPACE, STRING_BEGIN, VARIABLE, STRING_END, BACKQUOTE, STRING_END);

        // $(cd "`dirname "$0"`"/..; pwd)
        mockTest(fileTest, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, STRING_BEGIN, BACKQUOTE, WORD, WHITESPACE, STRING_BEGIN, VARIABLE, STRING_END, BACKQUOTE, STRING_END, WORD, RIGHT_PAREN);

        // "$(cd "`dirname "$0"`"/..; pwd)"
        mockTest(fileTest, STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, STRING_BEGIN, BACKQUOTE, WORD, WHITESPACE, STRING_BEGIN, VARIABLE, STRING_END, BACKQUOTE, STRING_END, WORD, RIGHT_PAREN, STRING_END);
    }

    @Test
    public void testIssue389() {
        // \\n is a line continuation
        mockTestSuccessWithErrors(fileTest, WHITESPACE);
    }

    @Test
    public void testIssue432() throws Exception {
        //mysql <<< "CREATE DATABASE dev" || echo hi
        mockTest(fileTest, WORD, WHITESPACE, REDIRECT_HERE_STRING, WHITESPACE, STRING_BEGIN, STRING_CONTENT, STRING_END, WHITESPACE, OR_OR, WORD);

        //mysql <<<"CREATE DATABASE dev"||echo hi
        mockTest(fileTest, WORD, WHITESPACE, REDIRECT_HERE_STRING, STRING_BEGIN, STRING_CONTENT, STRING_END, OR_OR, WORD);

        //mysql <<< 'CREATE DATABASE dev' || echo hi
        mockTest(fileTest, WORD, WHITESPACE, REDIRECT_HERE_STRING, WHITESPACE, STRING2, WHITESPACE, OR_OR, WORD, WORD);

        //mysql <<<"CREATE DATABASE dev"||echo hi
        mockTest(fileTest, WORD, WHITESPACE, REDIRECT_HERE_STRING, STRING2, OR_OR, WORD, WHITESPACE, WORD);

        //mysql <<< "CREATE DATABASE dev" && echo hi
        mockTest(fileTest, WORD, WHITESPACE, REDIRECT_HERE_STRING, STRING2, WHITESPACE, AND_AND, WHITESPACE, WORD, WORD);

        //cmd <<< 'hi'; echo hi2
        mockTest(fileTest, WORD, WHITESPACE, REDIRECT_HERE_STRING, STRING2, SEMI, WHITESPACE, WORD);

        //cmd <<< 'hi';echo hi2
        mockTest(fileTest, WORD, WHITESPACE, REDIRECT_HERE_STRING, STRING2, SEMI, WORD);

        //cmd <<< 'hi';echo && echo
        mockTest(fileTest, WORD, WHITESPACE, REDIRECT_HERE_STRING, STRING2, SEMI, WORD, WHITESPACE, AND_AND, WHITESPACE, WORD);

        //cmd <<< 'hi' & echo && echo
        mockTest(fileTest, WORD, WHITESPACE, REDIRECT_HERE_STRING, STRING2, WHITESPACE, AMP, WHITESPACE, WORD, WHITESPACE, AND_AND, WHITESPACE, WORD);
        //cmd <<< 'hi'& echo && echo
        mockTest(fileTest, WORD, WHITESPACE, REDIRECT_HERE_STRING, STRING2, AMP, WHITESPACE, WORD, WHITESPACE, AND_AND, WHITESPACE, WORD);
        //cmd <<< 'hi'&echo && echo
        mockTest(fileTest, WORD, WHITESPACE, REDIRECT_HERE_STRING, STRING2, AMP, WORD, WHITESPACE, AND_AND, WHITESPACE, WORD);
    }

    @Test
    public void testIssue460() throws Exception {
        //Bash 4
        //(a) |& a b
        mockTest(BashVersion.Bash_v4, fileTest, LEFT_PAREN, WORD, RIGHT_PAREN, WHITESPACE, PIPE_AMP, WHITESPACE, WORD, WHITESPACE, WORD);
    }

    @Test
    public void testBinary() throws Exception {
        mockTest(fileTest, Lists.newArrayList("exit"), WORD, LINE_FEED, WORD);
    }

    @Test
    public void testIssue401() throws Exception {
        mockTest(fileTest, STRING_BEGIN, DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_PERCENT, LESS_THAN, RIGHT_CURLY, STRING_END);
    }
}
