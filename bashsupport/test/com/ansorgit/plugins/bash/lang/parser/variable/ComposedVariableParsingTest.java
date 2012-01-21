package com.ansorgit.plugins.bash.lang.parser.variable;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.google.common.collect.Lists;
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

        //${@}
        mockTest(mockFunction, Lists.newArrayList("$", "{", "@"), DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_AT, RIGHT_CURLY);
        //${$}
        mockTest(mockFunction, Lists.newArrayList("$", "{", "$"), DOLLAR, LEFT_CURLY, DOLLAR, RIGHT_CURLY);
        //${#}
        mockTest(mockFunction, Lists.newArrayList("$", "{", "#"), DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        //${0}
        mockTest(mockFunction, Lists.newArrayList("$", "{", "0"), DOLLAR, LEFT_CURLY, NUMBER, RIGHT_CURLY);
        //${9}
        mockTest(mockFunction, Lists.newArrayList("$", "{", "9"), DOLLAR, LEFT_CURLY, NUMBER, RIGHT_CURLY);
        //${?}
        mockTest(mockFunction, Lists.newArrayList("$", "{", "?"), DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_QMARK, RIGHT_CURLY);
        //${!}
        mockTest(mockFunction, Lists.newArrayList("$", "{", "!"), DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_EXCL, RIGHT_CURLY);
        //${*}
        mockTest(mockFunction, Lists.newArrayList("$", "{", "*"), DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_STAR, RIGHT_CURLY);
        //${-}
        mockTest(mockFunction, Lists.newArrayList("$", "{", "-"), DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_MINUS, RIGHT_CURLY);
        //${_}
        mockTest(mockFunction, Lists.newArrayList("$", "{", "_"), DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
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
