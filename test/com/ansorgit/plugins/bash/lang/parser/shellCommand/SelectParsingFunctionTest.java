package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.google.common.collect.Lists;
import org.junit.Test;

@SuppressWarnings("Duplicates")
public class SelectParsingFunctionTest extends MockPsiTest {
    private MockPsiTest.MockFunction loopParser = new MockPsiTest.MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.selectParser.parse(psi);
        }
    };

    @Test
    public void testSelectLoop() {
        //select f in 1; do {
        //echo 1
        //}
        // done
        mockTest(loopParser,
                Lists.newArrayList("select", "f", "in"),
                SELECT_KEYWORD, WORD, WORD, INTEGER_LITERAL, SEMI, DO_KEYWORD, LEFT_CURLY, LINE_FEED,
                WORD, WHITESPACE, WORD, LINE_FEED, RIGHT_CURLY, WHITESPACE, LINE_FEED, DONE_KEYWORD);

        //select f in 1; do {
        //echo 1
        //};
        // done
        mockTest(loopParser,
                Lists.newArrayList("select", "f", "in"),
                SELECT_KEYWORD, WORD, WORD, INTEGER_LITERAL, SEMI, DO_KEYWORD, LEFT_CURLY, LINE_FEED,
                WORD, WHITESPACE, WORD, LINE_FEED, RIGHT_CURLY, SEMI, LINE_FEED, DONE_KEYWORD);

        //select A do echo $A; done
        mockTest(loopParser, SELECT_KEYWORD, WORD, DO_KEYWORD, WHITESPACE, WORD, VARIABLE, SEMI, DONE_KEYWORD);
    }

    @Test
    public void testErrors() throws Exception {
        //  select f in 1; do {
        //      echo 1
        //  } done
        //missing terminator after the body
        mockTestError(BashVersion.Bash_v3, loopParser,
                Lists.newArrayList("select", "f", "in"),
                SELECT_KEYWORD, WORD, WORD, INTEGER_LITERAL, SEMI, DO_KEYWORD, LEFT_CURLY, LINE_FEED,
                WORD, WHITESPACE, WORD, LINE_FEED, RIGHT_CURLY, WHITESPACE, DONE_KEYWORD);


        //select f in 1; do {
        //echo 1
        //} done
        //missing terminator after the body
        mockTestError(BashVersion.Bash_v3, loopParser,
                Lists.newArrayList("select", "f", "in"),
                SELECT_KEYWORD, WORD, WORD, INTEGER_LITERAL, SEMI, DO_KEYWORD, LEFT_CURLY, LINE_FEED,
                WORD, WHITESPACE, WORD, LINE_FEED, RIGHT_CURLY, WHITESPACE, DONE_KEYWORD);

    }

    @Test
    public void testIncompleteParse() throws Exception {
        //error markers must be present, but the incomplete if should be parsed without remaining elements

        // select f in a; do; done
        mockTestError(BashVersion.Bash_v3, loopParser, false, true,
                Lists.newArrayList("select", "f", "in"),
                SELECT_KEYWORD, WORD, WORD, WORD, SEMI, DO_KEYWORD, SEMI, DONE_KEYWORD);

        //select a in; do echo; done
        mockTestError(BashVersion.Bash_v3, loopParser, false, true,
                Lists.newArrayList("select", "f", "in"),
                SELECT_KEYWORD, WORD, WORD, SEMI, DO_KEYWORD, WORD, DONE_KEYWORD);
    }
}