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

package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.google.common.collect.Lists;
import org.junit.Test;

public class EvalCommandParsingTest extends MockPsiTest {
    private final MockFunction evalParsing = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return new EvalCommandParsing().parse(psi);
        }
    };

    @Test
    public void testParse() {
        //eval 'a=1'
        mockTest(evalParsing, Lists.newArrayList("eval", "'a=1'"), WORD, STRING2);

        //eval "a=1"
        mockTest(evalParsing, Lists.newArrayList("eval", "'a=1'"), WORD, STRING_BEGIN, STRING_CONTENT, STRING_END);

        //eval "echo" "abc" "$1"
        mockTest(evalParsing, Lists.newArrayList("eval", "'a=1'", "\"abc\"", "\"$1\""), WORD, STRING_BEGIN, STRING_CONTENT, STRING_END, STRING_BEGIN, STRING_CONTENT, STRING_END, STRING_BEGIN, STRING_CONTENT, STRING_END);

        //eval "" ""
        mockTest(evalParsing, Lists.newArrayList("eval", "\"\"", "\"\""), WORD, STRING_BEGIN, STRING_END, WHITESPACE, STRING_BEGIN, STRING_END);
    }

    @Test
    public void testIssue302() throws Exception {
        mockTest(evalParsing, Lists.newArrayList("eval"), WORD, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN);

        mockTest(evalParsing, Lists.newArrayList("eval"), WORD, DOLLAR, EXPR_ARITH, WORD, _EXPR_ARITH);
    }

    @Test
    public void testIssue330() throws Exception {
        //eval "$a=()"
        mockTest(evalParsing, Lists.newArrayList("eval"), WORD, WHITESPACE, STRING_BEGIN, VARIABLE, STRING_CONTENT, STRING_END);
    }

    @Test
    public void testIssue330Var() throws Exception {
        //eval "\${$a}"
        mockTest(evalParsing, Lists.newArrayList("eval"), WORD, WHITESPACE, STRING_BEGIN, STRING_CONTENT, VARIABLE, STRING_CONTENT, STRING_END);
    }

    @Test
    public void testIssue350() throws Exception {
        //eval "executable" 2>/dev/null
        mockTest(evalParsing, Lists.newArrayList("eval"), WORD, WHITESPACE, STRING_BEGIN, STRING_CONTENT, STRING_END, INTEGER_LITERAL, GREATER_THAN, WORD);
    }
}
