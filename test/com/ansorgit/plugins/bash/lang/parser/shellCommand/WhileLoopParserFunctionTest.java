package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

import java.util.Collections;

public class WhileLoopParserFunctionTest extends MockPsiTest {
    MockFunction loopCommand = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.whileParser.parse(psi);
        }
    };

    @Test
    public void testParse() throws Exception {
        // while a; do b; fi
        mockTest(loopCommand, WHILE_KEYWORD, WORD, SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD);
    }

    @Test
    public void testIncompleteParse() throws Exception {
        //error markers must be present, but the incomplete if should be parsed without remaining elements

        // while; do; done
        mockTestError(BashVersion.Bash_v3, loopCommand, false, true, Collections.<String>emptyList(), WHILE_KEYWORD, SEMI, DO_KEYWORD, SEMI, DONE_KEYWORD);

        // while a; do; done
        mockTestError(BashVersion.Bash_v3, loopCommand, false, true, Collections.<String>emptyList(), WHILE_KEYWORD, WORD, SEMI, DO_KEYWORD, SEMI, DONE_KEYWORD);

        // while; do a; done
        mockTestError(BashVersion.Bash_v3, loopCommand, false, true, Collections.<String>emptyList(), WHILE_KEYWORD, SEMI, DO_KEYWORD, WORD, SEMI, DONE_KEYWORD);
    }
}