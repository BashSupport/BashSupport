package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 31.03.11
 * Time: 19:45
 */
public class BacktickParsingFunctionTest extends MockPsiTest {
    MockFunction backtickParser = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.backtickParser.parse(psi);
        }
    };

    @Test
    public void testParsing() throws Exception {
        //``
        mockTest(backtickParser, BACKQUOTE, WORD, BACKQUOTE);

        //`echo`
        mockTest(backtickParser, BACKQUOTE, WORD, BACKQUOTE);

        //`$a`
        mockTest(backtickParser, BACKQUOTE, VARIABLE, BACKQUOTE);

        //`$((123))`
        mockTest(backtickParser, BACKQUOTE, DOLLAR, EXPR_ARITH, NUMBER, _EXPR_ARITH, BACKQUOTE);

        //`$[123]`
        mockTest(backtickParser, BACKQUOTE, DOLLAR, EXPR_ARITH_SQUARE, NUMBER, _EXPR_ARITH_SQUARE, BACKQUOTE);
    }
}
