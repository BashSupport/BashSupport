package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 23.06.11
 * Time: 16:37
 */
public class ConditionalCommandFunctionTest extends MockPsiTest {
    MockFunction conditionalFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.conditionalCommandParser.parse(psi);
        }
    };

    @Test
    public void testSingleTest() {
        //[[ a ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, _BRACKET_KEYWORD);
    }

    @Test
    public void testComposedCommand() {
        //[[ a && b ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, AND_AND, WORD, _BRACKET_KEYWORD);

        //[[ a || b ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, OR_OR, WORD, _BRACKET_KEYWORD);

        //[[ a || b && c ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, OR_OR, WORD, AND_AND, WORD, _BRACKET_KEYWORD);
    }

    @Test
    public void testNegationOperator() {
        //[[ !(a) ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, BANG_TOKEN, LEFT_PAREN, WORD, RIGHT_PAREN, _BRACKET_KEYWORD);
    }
}
