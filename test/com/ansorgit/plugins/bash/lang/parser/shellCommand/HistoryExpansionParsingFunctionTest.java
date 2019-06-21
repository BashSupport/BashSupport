package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * @author jansorg
 */
public class HistoryExpansionParsingFunctionTest extends MockPsiTest {
    private final MockFunction expansionParser = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.historyExpansionParser.parse(psi);
        }
    };

    @Test
    public void testSimpleExpansion() throws Exception {
        mockTest(expansionParser, Lists.newArrayList("!"), WORD, WORD);
    }

    @Test
    public void testNumbers() throws Exception {
        mockTest(expansionParser, Lists.newArrayList("!"), WORD, ARITH_NUMBER);
    }

    @Test
    public void testExpandString() throws Exception {
        //strings are expanded literally, i.e. without interpreting "x" as string content x
        mockTest(expansionParser, Lists.newArrayList("!"), WORD, STRING_BEGIN, STRING_CONTENT, STRING_END);
    }

    @Test
    public void testExpandBangToken() throws Exception {
        mockTest(expansionParser, Lists.newArrayList("!", "!"), WORD, WORD);
        mockTest(expansionParser, Lists.newArrayList("!"), WORD, DOLLAR);
    }

    @Test
    public void testSingleBangToken() throws Exception {
        mockTestFail(BashVersion.Bash_v3, expansionParser, Lists.newArrayList("!"), WORD);
    }
}
