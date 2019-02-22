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
import com.ansorgit.plugins.bash.lang.parser.misc.RedirectionParsing;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author jansorg
 */
public class RedirectionParsingTest extends MockPsiTest {
    private final MockFunction redirectionTest = new MockFunction() {
        public boolean apply(BashPsiBuilder builder) {
            RedirectionParsing.RedirectParseResult result = Parsing.redirection.parseRequiredListIfValid(builder, true);
            return result == RedirectionParsing.RedirectParseResult.OK || result == RedirectionParsing.RedirectParseResult.INVALID_REDIRECT;
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
        //2>OUT
        mockTest(redirectionTest, INTEGER_LITERAL, GREATER_THAN, WORD);
    }

    @Test
    public void testRedirectErrors() {
        //1 > out
        mockTestError(BashVersion.Bash_v3, redirectionTest, true, false, Arrays.asList("1", " ", ">", " ", "out"),
                INTEGER_LITERAL, WHITESPACE, GREATER_THAN, WHITESPACE, WORD);

        //1> &1
        mockTestSuccessWithErrors(redirectionTest, Arrays.asList("1", ">", " ", "&1"),
                INTEGER_LITERAL, GREATER_THAN, WHITESPACE, FILEDESCRIPTOR);

        //>>&1
        mockTestSuccessWithErrors(BashVersion.Bash_v3, redirectionTest, Arrays.asList(">>", "&1"),
                SHIFT_RIGHT, FILEDESCRIPTOR);

        //1>>&1
        mockTestSuccessWithErrors(BashVersion.Bash_v3, redirectionTest, Arrays.asList("1", ">>", "&1"),
                INTEGER_LITERAL, SHIFT_RIGHT, FILEDESCRIPTOR);

        //<<&1
        mockTestError(BashVersion.Bash_v3, redirectionTest, Arrays.asList("<<", "&1"),
                HEREDOC_MARKER_TAG, FILEDESCRIPTOR);

        //1<<&1
        mockTestError(BashVersion.Bash_v3, redirectionTest, Arrays.asList("1", "<<", "&1"),
                INTEGER_LITERAL, HEREDOC_MARKER_TAG, FILEDESCRIPTOR);

        //<<<&1
        mockTestSuccessWithErrors(BashVersion.Bash_v3, redirectionTest, Arrays.asList("<<<", "&1"),
                REDIRECT_HERE_STRING, FILEDESCRIPTOR);
    }

    @Test
    public void testBash4Redirect() {
        mockTest(BashVersion.Bash_v4, redirectionTest, REDIRECT_AMP_GREATER_GREATER, WORD);
    }

    @Test
    public void testProcessSubstitution() {
        // < <(true)
        mockTest(redirectionTest, LESS_THAN, WHITESPACE, LESS_THAN, LEFT_PAREN, WORD, RIGHT_PAREN);
        // > >(true)
        mockTest(redirectionTest, GREATER_THAN, WHITESPACE, GREATER_THAN, LEFT_PAREN, WORD, RIGHT_PAREN);
        // < <(true && false)
        mockTest(redirectionTest, LESS_THAN, WHITESPACE, LESS_THAN, LEFT_PAREN, WORD, WHITESPACE, AND_AND, WORD, RIGHT_PAREN);
        // > >(true && false)
        mockTest(redirectionTest, GREATER_THAN, WHITESPACE, GREATER_THAN, LEFT_PAREN, WORD, WHITESPACE, AND_AND, WORD, RIGHT_PAREN);

        // < < (true)
        mockTestSuccessWithErrors(redirectionTest, LESS_THAN, WHITESPACE, LESS_THAN, WHITESPACE, LEFT_PAREN, WORD, RIGHT_PAREN);
        // > > (true)
        mockTestSuccessWithErrors(redirectionTest, GREATER_THAN, WHITESPACE, GREATER_THAN, WHITESPACE, LEFT_PAREN, WORD, RIGHT_PAREN);
    }

    @Test
    public void testHereStrings() throws Exception {
        mockTest(redirectionTest, REDIRECT_HERE_STRING, WORD);
        mockTest(redirectionTest, REDIRECT_HERE_STRING, WORD, WORD);
        mockTest(redirectionTest, REDIRECT_HERE_STRING, STRING2);
        mockTest(redirectionTest, REDIRECT_HERE_STRING, STRING_BEGIN, STRING_CONTENT, STRING_END);

        //mysql <<< "CREATE DATABASE dev" || echo hi
        mockTest(redirectionTest, REDIRECT_HERE_STRING, WHITESPACE, STRING_BEGIN, STRING_CONTENT, STRING_END);
        //mysql <<<"CREATE DATABASE dev"||echo hi
        mockTest(redirectionTest, REDIRECT_HERE_STRING, STRING_BEGIN, STRING_CONTENT, STRING_END);

        //mysql <<< 'CREATE DATABASE dev' || echo hi
        mockTest(redirectionTest, REDIRECT_HERE_STRING, WHITESPACE, STRING2);
        //mysql <<<"CREATE DATABASE dev"||echo hi
        mockTest(redirectionTest, REDIRECT_HERE_STRING, STRING2);

        //mysql <<< "CREATE DATABASE dev" && echo hi
        mockTest(redirectionTest, REDIRECT_HERE_STRING, STRING2);
    }

    @Test
    public void testSimpleListParsingWithErrors() {
        //>> >> a
        mockTestSuccessWithErrors(redirectionTest, SHIFT_RIGHT, SHIFT_RIGHT, WORD);

        //a > a a
        mockTestError(redirectionTest, WORD, PIPE, WORD, WORD);

        //|
        mockTestError(redirectionTest, PIPE);
    }
}
