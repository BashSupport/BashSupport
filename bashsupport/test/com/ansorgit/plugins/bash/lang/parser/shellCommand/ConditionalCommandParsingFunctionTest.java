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
public class ConditionalCommandParsingFunctionTest extends MockPsiTest {
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

        //[[ -z "" ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, COND_OP, STRING_BEGIN, STRING_END, _BRACKET_KEYWORD);

        //[[ a ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, _BRACKET_KEYWORD);

        //[[ $(echo a) ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WHITESPACE, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN, WHITESPACE, _BRACKET_KEYWORD);
    }

    @Test
    public void testRegExp() throws Exception {
        //[[ a =~ abc ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, WHITESPACE, COND_OP_REGEX, WHITESPACE, WORD, _BRACKET_KEYWORD);

        //[[ a =~ ..e*x ]]
        //mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, WHITESPACE, COND_OP_REGEX, WHITESPACE, WORD, _BRACKET_KEYWORD);

        //[[ a =~ ^$ ]]
        //mockTest(conditionalFunction, BRACKET_KEYWORD, WORD, WHITESPACE, COND_OP_REGEX, WHITESPACE, WORD, _BRACKET_KEYWORD);
    }

    @Test
    public void testErrors() throws Exception {
        //fixme: [ ]
        //mockTest(conditionalCommandParserTest, EXPR_CONDITIONAL, WHITESPACE, _EXPR_CONDITIONAL);
        //fixme:[[ ]]
        //mockTestError(conditionalCommandParserTest, BRACKET_KEYWORD, _BRACKET_KEYWORD);
    }

    @Test
    public void testNegationOperator() {
        //[[ !(a) ]]
        mockTest(conditionalFunction, BRACKET_KEYWORD, COND_OP_NOT, LEFT_PAREN, WORD, RIGHT_PAREN, _BRACKET_KEYWORD);
    }
}
