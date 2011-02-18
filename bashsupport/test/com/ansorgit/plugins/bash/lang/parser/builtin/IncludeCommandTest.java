package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 18.02.11
 * Time: 19:47
 */
public class IncludeCommandTest extends MockPsiTest {
    MockFunction parserFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return new IncludeCommand().parse(psi);
        }
    };

    @Test
    public void testParseSimpleDot() throws Exception {
        //. a
        mockTest(parserFunction, Lists.newArrayList("."), INTERNAL_COMMAND, WORD);

        //. "a"
        mockTest(parserFunction, Lists.newArrayList("."), INTERNAL_COMMAND, STRING_BEGIN, WORD, STRING_END);

        //. "a" abc def
        mockTest(parserFunction, Lists.newArrayList("."), INTERNAL_COMMAND, STRING_BEGIN, WORD, STRING_END, WORD, WORD);
    }

    @Test
    public void testParseSimpleSource() throws Exception {
        //source a
        mockTest(parserFunction, Lists.newArrayList("source"), INTERNAL_COMMAND, WORD);

        //source "a"
        mockTest(parserFunction, Lists.newArrayList("source"), INTERNAL_COMMAND, STRING_BEGIN, WORD, STRING_END);

        //source "a" abc def
        mockTest(parserFunction, Lists.newArrayList("source"), INTERNAL_COMMAND, STRING_BEGIN, WORD, STRING_END, WORD, WORD);
    }

    @Test
    public void testParseErrors() {
        //.
        mockTestFail(BashVersion.Bash_v4, parserFunction, INTERNAL_COMMAND);

        //.
        mockTestError(BashVersion.Bash_v4, parserFunction, Lists.newArrayList("."), INTERNAL_COMMAND);

        //source
        mockTestError(BashVersion.Bash_v4, parserFunction, Lists.newArrayList("source"), INTERNAL_COMMAND);
    }
}
