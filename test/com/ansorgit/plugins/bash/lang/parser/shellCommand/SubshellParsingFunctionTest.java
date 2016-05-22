package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

public class SubshellParsingFunctionTest extends MockPsiTest {
    MockFunction subshellCommand = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.subshellParser.parse(psi);
        }
    };


    @Test
    public void testParse() throws Exception {
        //$()
        mockTest(subshellCommand, LEFT_PAREN, RIGHT_PAREN);
    }

    @Test
    public void testIssue341(){
        // $(cd "`dirname "$0"`")
        mockTest(subshellCommand, LEFT_PAREN, WORD, WHITESPACE, STRING_BEGIN, BACKQUOTE, WORD, WHITESPACE, STRING_BEGIN, VARIABLE, STRING_END, BACKQUOTE, STRING_END, RIGHT_PAREN);
    }
}