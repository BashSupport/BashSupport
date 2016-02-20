package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

import java.util.Collections;

public class IfParsingFunctionTest extends MockPsiTest {
    MockFunction ifCommand = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.ifParser.parse(psi);
        }
    };

    @Test
    public void testParse() throws Exception {
        // if a; then b; fi
        mockTest(ifCommand, IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, SEMI, FI_KEYWORD);

        // if a; then b
        // fi
        mockTest(ifCommand, IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, LINE_FEED, FI_KEYWORD);

        // if a; then b; else c; fi
        mockTest(ifCommand, IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, SEMI, ELSE_KEYWORD, WORD, SEMI, FI_KEYWORD);

        // if a; then b
        // else c; fi
        mockTest(ifCommand, IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, LINE_FEED, ELSE_KEYWORD, WORD, SEMI, FI_KEYWORD);

        // if a; then b
        // else c;
        // fi
        mockTest(ifCommand, IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, LINE_FEED, ELSE_KEYWORD, WORD, SEMI, LINE_FEED, FI_KEYWORD);

        //this is not an if command with a parsed else, there is no semicolon after the first command
        //it can be parsed but must make sure that the else is not parsed as keyword
        // if echo a; then echo b else echo c; fi
        mockTest(ifCommand, IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, ELSE_KEYWORD, WORD, SEMI, FI_KEYWORD);
    }

    @Test
    public void testIncompleteParse() throws Exception {
        //error markers must be present, but the incomplete if should be parsed without remaining elements

        // if; then; fi
        mockTestError(BashVersion.Bash_v3, ifCommand, false, true, Collections.<String>emptyList(), IF_KEYWORD, SEMI, THEN_KEYWORD, SEMI, FI_KEYWORD);

        // if a; then; fi
        mockTestError(BashVersion.Bash_v3, ifCommand, false, true, Collections.<String>emptyList(), IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, SEMI, FI_KEYWORD);

        // if a; then a; else; fi
        mockTestError(BashVersion.Bash_v3, ifCommand, false, true, Collections.<String>emptyList(), IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, WORD, SEMI, ELSE_KEYWORD, SEMI, FI_KEYWORD);

        // if a; then; else; fi
        mockTestError(BashVersion.Bash_v3, ifCommand, false, true, Collections.<String>emptyList(), IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, SEMI, ELSE_KEYWORD, SEMI, FI_KEYWORD);

        // if a; then; elif; then; else; fi
        mockTestError(BashVersion.Bash_v3, ifCommand, false, true, Collections.<String>emptyList(), IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, SEMI, ELIF_KEYWORD, SEMI, THEN_KEYWORD, SEMI, ELSE_KEYWORD, SEMI, FI_KEYWORD);

        // if a; then; elif then; else; fi
        mockTestError(BashVersion.Bash_v3, ifCommand, false, true, Collections.<String>emptyList(), IF_KEYWORD, WORD, SEMI, THEN_KEYWORD, SEMI, ELIF_KEYWORD, THEN_KEYWORD, SEMI, ELSE_KEYWORD, SEMI, FI_KEYWORD);
    }

}