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

package com.ansorgit.plugins.bash.lang.parser.parameterExpansion;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

/**
 * @author jansorg
 */
public class ParameterExpansionParsingTest extends MockPsiTest {
    MockFunction expansionParser = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.parameterExpansionParsing.parse(psi);
        }
    };

    @Test
    public void testParse() throws Exception {
        //{}
        mockTestSuccessWithErrors(expansionParser, LEFT_CURLY, RIGHT_CURLY);

        //{A}
        mockTest(expansionParser, LEFT_CURLY, WORD, RIGHT_CURLY);

        //{@}
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_AT, RIGHT_CURLY);

        //{?}
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_QMARK, RIGHT_CURLY);

        //{B:-B}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_MINUS, WORD, RIGHT_CURLY);

        //{$}
        mockTest(expansionParser, LEFT_CURLY, DOLLAR, RIGHT_CURLY);

        //{$:-x} means the pid with substitution operator
        mockTest(expansionParser, LEFT_CURLY, DOLLAR, PARAM_EXPANSION_OP_COLON_MINUS, WORD, RIGHT_CURLY);
    }

    @Test
    public void testParseSubstitution() throws Exception {
        //{B:-a b c}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_MINUS, WORD, WHITESPACE, WORD, WHITESPACE, WORD, RIGHT_CURLY);

        //{B:+a b c}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_PLUS, WORD, WHITESPACE, WORD, WHITESPACE, WORD, RIGHT_CURLY);

        //{B:?a b c}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_QMARK, WORD, WHITESPACE, WORD, WHITESPACE, WORD, RIGHT_CURLY);

        //{B:=a b c}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_EQ, WORD, WHITESPACE, WORD, WHITESPACE, WORD, RIGHT_CURLY);
    }

    @Test
    public void testParseSubstitution2() throws Exception {
        //{B:-${a}}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_MINUS, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, RIGHT_CURLY);
    }

    @Test
    public void testParseAssignment() throws Exception {
        //{A=x}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_EQ, RIGHT_CURLY);

        //{A:=x}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_EQ, RIGHT_CURLY);
    }

    @Test
    public void testParseLengthExpansion() throws Exception {
        //{#a}
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_HASH, WORD, RIGHT_CURLY);
    }

    @Test
    public void testParseArrayVarRef() throws Exception {
        //{a[1]}
        mockTest(expansionParser, LEFT_CURLY, WORD, LEFT_SQUARE, ARITH_NUMBER, RIGHT_SQUARE, RIGHT_CURLY);

        //{a[1*34-4]}
        mockTest(expansionParser, LEFT_CURLY, WORD, LEFT_SQUARE, ARITH_NUMBER, ARITH_MULT, ARITH_NUMBER, ARITH_MINUS, ARITH_NUMBER, RIGHT_SQUARE, RIGHT_CURLY);
    }

    @Test
    public void testParseInvalidLengthExpansion() throws Exception {
        //{# a}
        mockTestError(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_HASH, WHITESPACE, WORD, RIGHT_CURLY);
    }

    @Test
    public void testParseAdvanced() {
        //{a:$(a $(b)/..)}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_UNKNOWN, DOLLAR, LEFT_PAREN, WORD,
                WHITESPACE, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN,
                WORD, RIGHT_PAREN, RIGHT_CURLY);

        //{a:${a:a}}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_UNKNOWN, DOLLAR, LEFT_CURLY,
                WORD, PARAM_EXPANSION_OP_UNKNOWN, WORD, RIGHT_CURLY, RIGHT_CURLY);

        //{a:"a"}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_UNKNOWN, STRING_BEGIN, STRING_CONTENT, STRING_END, RIGHT_CURLY);

        //{!a=x}
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_EXCL, WORD, PARAM_EXPANSION_OP_EQ, WORD, RIGHT_CURLY);
    }

    @Test
    public void testArithmeticArrayRefs() throws Exception {
        //{var[1+2-var]}
        mockTest(expansionParser, LEFT_CURLY, WORD, LEFT_SQUARE, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, ARITH_MINUS, WORD, RIGHT_SQUARE, RIGHT_CURLY);

        //{#var[1+2-var]}
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_HASH, WORD, LEFT_SQUARE, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, ARITH_MINUS, WORD, RIGHT_SQUARE, RIGHT_CURLY);

        //{#var[1+2-var]#[a-z]}
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_HASH, WORD, LEFT_SQUARE, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, ARITH_MINUS, WORD, RIGHT_SQUARE,
                PARAM_EXPANSION_OP_HASH, LEFT_SQUARE, WORD, PARAM_EXPANSION_OP_MINUS, WORD, RIGHT_SQUARE,
                RIGHT_CURLY);

        //{#A[1]}
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_HASH, WORD, LEFT_SQUARE, ARITH_NUMBER, RIGHT_SQUARE, RIGHT_CURLY);
    }

    @Test
    public void testComposedDefaultValue() throws Exception {
        //{a-"(x)"}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_MINUS, STRING_BEGIN, STRING_CONTENT, STRING_END, RIGHT_CURLY);

        //{a-(x)} is a valid expression, same as {a-"(x)"}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_MINUS, LEFT_PAREN, WORD, RIGHT_PAREN, RIGHT_CURLY);

        //${x-`$[1]`}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_MINUS, BACKQUOTE, DOLLAR, EXPR_ARITH_SQUARE, ARITH_NUMBER, _EXPR_ARITH_SQUARE, BACKQUOTE, RIGHT_CURLY);
    }


    @Test
    public void testParseReplace() throws Exception {
        // {myvar/,/ }
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_SLASH, WORD, PARAM_EXPANSION_OP_SLASH, WHITESPACE, RIGHT_CURLY);
    }

    @Test
    public void testParseArray() throws Exception {
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_AT, PARAM_EXPANSION_OP_COLON, WORD, RIGHT_CURLY);
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_STAR, PARAM_EXPANSION_OP_COLON, WORD, RIGHT_CURLY);
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON, WORD, RIGHT_CURLY);
    }

    @Test
    public void testIssue265() throws Exception {
        //{#}
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_HASH, RIGHT_CURLY);
        //{#}
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_HASH_HASH, RIGHT_CURLY);
    }

    @Test
    public void testIssue272() throws Exception {
        //{#array_var[@]}
        mockTest(expansionParser, LEFT_CURLY, PARAM_EXPANSION_OP_HASH, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_AT, RIGHT_SQUARE, RIGHT_CURLY);
    }

    @Test
    public void testIssue312() throws Exception {
        //${A/a/x}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_SLASH, WORD, PARAM_EXPANSION_OP_SLASH, WORD, RIGHT_CURLY);

        //${A/\n/ }
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_SLASH, LINE_FEED, PARAM_EXPANSION_OP_SLASH, WHITESPACE, RIGHT_CURLY);

        //${A//\n/x}
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_SLASH, PARAM_EXPANSION_OP_SLASH, LINE_FEED, PARAM_EXPANSION_OP_SLASH, WHITESPACE, RIGHT_CURLY);
    }

    @Test
    public void testIssue401() throws Exception {
        mockTest(expansionParser, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_PERCENT, LESS_THAN, RIGHT_CURLY);
    }
}
