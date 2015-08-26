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
}