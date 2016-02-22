package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

import java.util.Collections;

public class UntilLoopParserFunctionTest extends MockPsiTest {
    MockFunction loopCommand = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.untilParser.parse(psi);
        }
    };

    @Test
    public void testParse() throws Exception {
        // until a; do b; fi
        mockTest(loopCommand, UNTIL_KEYWORD, WORD, SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD);
    }

    @Test
    public void testIncompleteParse() throws Exception {
        //error markers must be present, but the incomplete if should be parsed without remaining elements

        // until; do; done
        mockTestError(BashVersion.Bash_v3, loopCommand, false, true, Collections.<String>emptyList(), UNTIL_KEYWORD, SEMI, DO_KEYWORD, SEMI, DONE_KEYWORD);

        // until a; do; done
        mockTestError(BashVersion.Bash_v3, loopCommand, false, true, Collections.<String>emptyList(), UNTIL_KEYWORD, WORD, SEMI, DO_KEYWORD, SEMI, DONE_KEYWORD);

        // until; do a; done
        mockTestError(BashVersion.Bash_v3, loopCommand, false, true, Collections.<String>emptyList(), UNTIL_KEYWORD, SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD);
    }
}