package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.google.common.collect.Lists;
import org.junit.Test;

public class TrapCommandParsingFunctionTest extends MockPsiTest {
    MockFunction trapCommand = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.trapCommandParser.parse(psi);
        }
    };


    @Test
    public void testParse() throws Exception {
        //trap
        mockTest(trapCommand, TRAP_KEYWORD);

        //trap -lp
        mockTest(trapCommand, Lists.newArrayList("trap", "-lp"), TRAP_KEYWORD, WORD);

        //trap -p SIGINT
        mockTest(trapCommand, Lists.newArrayList("trap", "-p", "SIGINT"), TRAP_KEYWORD, WORD, WORD);

        //trap functionName SIGINT
        mockTest(trapCommand, Lists.newArrayList("trap", "functioName", "SIGINT"), TRAP_KEYWORD, WORD, WORD, WORD);

        //trap functionName 'SIGINT'
        mockTest(trapCommand, TRAP_KEYWORD, WORD, WORD, STRING2);
    }
}