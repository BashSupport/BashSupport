/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: RedirectionParsingTest.java, Class: RedirectionParsingTest
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
import org.junit.Test;

import java.util.Arrays;

/**
 * Date: 24.03.2009
 * Time: 21:56:59
 *
 * @author Joachim Ansorg
 */
public class RedirectionParsingTest extends MockPsiTest {
    MockFunction redirectionTest = new MockFunction() {
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.redirection.parseList(builder, false);
        }
    };

    @Test
    public void testSimpleListParsing() {
        //>a
        mockTest(redirectionTest, GREATER_THAN, WORD);
        //1>a
        mockTest(redirectionTest, INTEGER_LITERAL, GREATER_THAN, WORD);
        //>>a
        mockTest(redirectionTest, SHIFT_RIGHT, WORD);
        //>$a
        mockTest(redirectionTest, GREATER_THAN, VARIABLE);
        //>>a>a
        mockTest(redirectionTest, SHIFT_RIGHT, WORD, GREATER_THAN, WORD);
        //>&1
        mockTest(redirectionTest, GREATER_THAN, FILEDESCRIPTOR);
        //>>1
        mockTest(redirectionTest, SHIFT_RIGHT, INTEGER_LITERAL);
        //>> 1
        mockTest(redirectionTest, SHIFT_RIGHT, WHITESPACE, INTEGER_LITERAL);
        //>& 1
        mockTest(BashVersion.Bash_v3, redirectionTest, 3, Arrays.asList(">&", " ", "1"),
                REDIRECT_GREATER_AMP, WHITESPACE, INTEGER_LITERAL);
    }

    @Test
    public void testRedirectErrors() {
        //1 > out
        mockTestError(BashVersion.Bash_v3, redirectionTest, Arrays.asList("1", " ", ">", " ", "out"),
                INTEGER_LITERAL, WHITESPACE, GREATER_THAN, WHITESPACE, WORD);
        //1> &1
        mockTestError(BashVersion.Bash_v3, redirectionTest, Arrays.asList("1", ">", " ", "&1"),
                INTEGER_LITERAL, GREATER_THAN, WHITESPACE, FILEDESCRIPTOR);
        //>>&1
        mockTestError(BashVersion.Bash_v3, redirectionTest, Arrays.asList(">>", "&1"),
                SHIFT_RIGHT, FILEDESCRIPTOR);
        //1>>&1
        mockTestError(BashVersion.Bash_v3, redirectionTest, Arrays.asList("1", ">>", "&1"),
                INTEGER_LITERAL, SHIFT_RIGHT, FILEDESCRIPTOR);
        //<<&1
        mockTestError(BashVersion.Bash_v3, redirectionTest, Arrays.asList("<<", "&1"),
                REDIRECT_LESS_LESS, FILEDESCRIPTOR);
        //1<<&1
        mockTestError(BashVersion.Bash_v3, redirectionTest, Arrays.asList("1", "<<", "&1"),
                INTEGER_LITERAL, REDIRECT_LESS_LESS, FILEDESCRIPTOR);
        //<<<&1
        mockTestError(BashVersion.Bash_v3, redirectionTest, Arrays.asList("<<<", "&1"),
                REDIRECT_LESS_LESS_LESS, FILEDESCRIPTOR);
    }

    @Test
    public void testBash4Redirect() {
        mockTest(BashVersion.Bash_v4, redirectionTest, REDIRECT_AMP_GREATER_GREATER, WORD);
    }

    @Test
    public void testBash4Errors() {
        mockTestError(BashVersion.Bash_v4, redirectionTest, PIPE_AMP);
    }

    @Test
    public void testSimpleListParsingWithErrors() {
        mockTestError(redirectionTest, SHIFT_RIGHT, SHIFT_RIGHT, WORD);
        mockTestError(redirectionTest, PIPE);
        mockTestError(redirectionTest, WORD, PIPE, WORD, WORD);
    }
}
