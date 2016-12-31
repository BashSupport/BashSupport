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

package com.ansorgit.plugins.bash.lang.parser.command;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import org.junit.Test;

public class SimpleCommandParsingFunctionTest extends MockPsiTest {
    public final SimpleCommandParsingFunction simpleCommandParser = new SimpleCommandParsingFunction();

    MockFunction function = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return simpleCommandParser.parse(psi);
        }
    };

    @Test
    public void testParsingArrayVar() throws Exception {
        //a=(['x']=1)
        mockTest(function, ASSIGNMENT_WORD, EQ, LEFT_PAREN, LEFT_SQUARE, STRING2, RIGHT_SQUARE, EQ, WORD, RIGHT_PAREN);

        //a=(["x"]=1)
        mockTest(function, ASSIGNMENT_WORD, EQ, LEFT_PAREN, LEFT_SQUARE, STRING_BEGIN, STRING_CONTENT, STRING_END, RIGHT_SQUARE, EQ, WORD, RIGHT_PAREN);

        //${x[ ( 1+1 ) ]}
        mockTest(function, DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, LEFT_PAREN, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, RIGHT_PAREN, RIGHT_SQUARE, RIGHT_CURLY);

        //${x[ (( 1+1 )) ]}
        mockTest(function, DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, EXPR_ARITH, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, _EXPR_ARITH, RIGHT_SQUARE, RIGHT_CURLY);
    }

    @Test
    public void testDollarCommand() throws Exception {
        //$x=$x
        mockTest(function, VARIABLE, EQ, VARIABLE);
    }

    @Test
    public void testCommandWithErrors() throws Exception {
        // ${=1}
        mockTestSuccessWithErrors(function, DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_EQ, INTEGER_LITERAL, RIGHT_CURLY);

        // echo ${=1}
        mockTestSuccessWithErrors(function, WORD, DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_EQ, INTEGER_LITERAL, RIGHT_CURLY);
    }
}