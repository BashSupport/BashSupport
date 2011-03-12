package com.ansorgit.plugins.bash.lang.parser.variable;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 26.01.11
 * Time: 19:58
 */
public class ComposedVariableParsingTest extends MockPsiTest {
    protected MockFunction mockFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return new ComposedVariableParsing().parse(psi);
        }
    };

    @Test
    public void testParsing() throws Exception {
        //$[1]
        mockTest(mockFunction, DOLLAR, EXPR_ARITH_SQUARE, NUMBER, _EXPR_ARITH_SQUARE);
    }

    @Test
    public void testArrayParsing() throws Exception {
        //${a[*]}
        mockTest(mockFunction, DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_STAR, RIGHT_SQUARE, RIGHT_CURLY);

        //${a[@]}
        mockTest(mockFunction, DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_AT, RIGHT_SQUARE, RIGHT_CURLY);

        //${a[0]}
        mockTest(mockFunction, DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, WORD, RIGHT_SQUARE, RIGHT_CURLY);

        //${a[@]:1}
        mockTest(mockFunction, DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, WORD, RIGHT_SQUARE, PARAM_EXPANSION_OP_COLON, WORD, RIGHT_CURLY);
    }
}
