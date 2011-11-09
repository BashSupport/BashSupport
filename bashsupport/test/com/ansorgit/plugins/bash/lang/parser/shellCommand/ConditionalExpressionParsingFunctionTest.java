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

    @Test
    public void testAdvancedExpressions() throws Exception {
        //[ -z a ]
        mockTest(conditionalFunction, EXPR_CONDITIONAL, COND_OP, WORD, _EXPR_CONDITIONAL);
        //[ a ]
        mockTest(conditionalFunction, EXPR_CONDITIONAL, WORD, _EXPR_CONDITIONAL);
        //[ $a ]
        mockTest(conditionalFunction, EXPR_CONDITIONAL, VARIABLE, _EXPR_CONDITIONAL);
        //[ $(a) = a ]
        mockTest(conditionalFunction, EXPR_CONDITIONAL, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN, EQ, WORD, _EXPR_CONDITIONAL);
        //[ `echo a` ]
        mockTest(conditionalFunction, EXPR_CONDITIONAL, BACKQUOTE, WORD, WORD, BACKQUOTE, _EXPR_CONDITIONAL);
        //[ \${a} ]
        mockTest(conditionalFunction, EXPR_CONDITIONAL, WHITESPACE, WORD, LEFT_CURLY, WORD, RIGHT_CURLY, WHITESPACE, _EXPR_CONDITIONAL);
        //[ a  ] 
        mockTest(conditionalFunction, EXPR_CONDITIONAL, WORD, WHITESPACE, _EXPR_CONDITIONAL);
        //[ a  ]
        mockTest(conditionalFunction, EXPR_CONDITIONAL, WORD, WHITESPACE, _EXPR_CONDITIONAL);
        //[[ $(a)  ]]
        mockTest(conditionalFunction, EXPR_CONDITIONAL, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, WHITESPACE, _EXPR_CONDITIONAL);
    }

    @Test
    public void testConditionalError() {
        //[ if a; then b; fi ]
        mockTestError(conditionalFunction, EXPR_CONDITIONAL, IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, SEMI, FI_KEYWORD, _EXPR_CONDITIONAL);
    }

}

