package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 09.11.11
 * Time: 21:16
 */
public class ConditionalExpressionParsingFunctionTest extends MockPsiTest {
    MockPsiTest.MockFunction conditionalFunction = new MockPsiTest.MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.conditionalExpressionParser.parse(psi);
        }
    };

    @Test
    public void testSingleTest() {
        //[ x ]
        mockTest(conditionalFunction, EXPR_CONDITIONAL, WORD, _EXPR_CONDITIONAL);

        //[ -f x ]
        mockTest(conditionalFunction, EXPR_CONDITIONAL, COND_OP, WHITESPACE, WORD, _EXPR_CONDITIONAL);
    }
}
