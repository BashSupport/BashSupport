/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: FileParsingTest.java, Class: FileParsingTest
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
}
