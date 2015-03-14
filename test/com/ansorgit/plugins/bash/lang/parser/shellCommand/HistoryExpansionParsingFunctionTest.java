package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

/**
 * @author Joachim Ansorg
 */
public class HistoryExpansionParsingFunctionTest extends MockPsiTest {
    private MockFunction expansionParser = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.historyExpansionParser.parse(psi);
        }
    };

    @Test
    public void testSimpleExpansion() throws Exception {
        mockTest(expansionParser, BANG_TOKEN, WORD);
    }

    @Test
    public void testNumbers() throws Exception {
        mockTest(expansionParser, BANG_TOKEN, NUMBER);
    }

    @Test
    public void testExpandString() throws Exception {
        //strings are expanded literally, i.e. without interpreting "x" as string content x
        mockTest(expansionParser, BANG_TOKEN, STRING_BEGIN, WORD, STRING_END);
    }

    @Test
    public void testExpandBangToken() throws Exception {
        mockTest(expansionParser, BANG_TOKEN, BANG_TOKEN);
        mockTest(expansionParser, BANG_TOKEN, DOLLAR);
    }

    @Test
    public void testSingleBangToken() throws Exception {
        mockTestFail(expansionParser, BANG_TOKEN);
    }
}
