package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.parser.BashParser;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.FileParsing;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * @author jansorg
 */
public class IncludeCommandTest extends MockPsiTest {
    MockFunction parserFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return new IncludeCommand().parse(psi);
        }
    };

    MockFunction fileParsingFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return new FileParsing().parseFile(psi);
        }
    };

    @Test
    public void testParseSimpleDot() throws Exception {
        //. a
        mockTest(parserFunction, Lists.newArrayList("."), WORD, WORD);

        //. "a"
        mockTest(parserFunction, Lists.newArrayList("."), WORD, STRING_BEGIN, STRING_CONTENT, STRING_END);

        //. "a" abc def
        mockTest(parserFunction, Lists.newArrayList("."), WORD, STRING_BEGIN, STRING_CONTENT, STRING_END, WORD, WORD);

        //. x >& y
        mockTest(parserFunction, Lists.newArrayList("."), WORD, WORD, REDIRECT_GREATER_AMP, WORD);

        //fixme
        //>& x . x
        //mockTest(parserFunction, Lists.newArrayList(">&", "x", "."), REDIRECT_GREATER_AMP, WORD, WORD, WORD);
    }

    @Test
    public void testParseSimpleSource() throws Exception {
        //source a
        mockTest(parserFunction, Lists.newArrayList("source"), WORD, WORD);

        //source "a"
        mockTest(parserFunction, Lists.newArrayList("source"), WORD, STRING_BEGIN, STRING_CONTENT, STRING_END);

        //source "a" abc def
        mockTest(parserFunction, Lists.newArrayList("source"), WORD, STRING_BEGIN, STRING_CONTENT, STRING_END, WORD, WORD);

        //source=x is not a valid source statement, but a valid assignment
        mockTestError(BashVersion.Bash_v4, parserFunction, Lists.newArrayList("source"), WORD, EQ, WORD);
        mockTest(fileParsingFunction, Lists.newArrayList("source"), WORD, EQ, WORD);
    }

    @Test
    public void testParseErrors() {
        //.
        mockTestFail(BashVersion.Bash_v4, parserFunction, WORD);

        //.
        mockTestError(BashVersion.Bash_v4, parserFunction, Lists.newArrayList("."), WORD);

        //source
        mockTestError(BashVersion.Bash_v4, parserFunction, Lists.newArrayList("source"), WORD);
    }
}
