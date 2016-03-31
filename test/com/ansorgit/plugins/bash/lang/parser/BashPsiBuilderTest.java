package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.BashVersion;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * @author jansorg
 */
public class BashPsiBuilderTest extends MockPsiTest {
    @Test
    public void testWhitespaceDisabled() throws Exception {
        BashPsiBuilder builder = new BashPsiBuilder(null, builderFor(Collections.<String>emptyList(), ARITH_NUMBER, WHITESPACE, WORD), BashVersion.Bash_v4);

        Assert.assertEquals(ARITH_NUMBER, builder.getTokenType());

        builder.advanceLexer();
        Assert.assertEquals(WORD, builder.getTokenType());
    }

    @Test
    public void testWhitespaceEnabled() throws Exception {
        BashPsiBuilder builder = new BashPsiBuilder(null, builderFor(Collections.<String>emptyList(), ARITH_NUMBER, WHITESPACE, WORD, WHITESPACE, ARITH_NUMBER), BashVersion.Bash_v4);
        Assert.assertEquals(ARITH_NUMBER, builder.getTokenType());

        builder.advanceLexer();
        Assert.assertEquals(WHITESPACE, builder.getTokenType(true));

        builder.advanceLexer();
        Assert.assertEquals(WORD, builder.getTokenType(true));

        builder.advanceLexer();
        Assert.assertEquals(ARITH_NUMBER, builder.getTokenType(false));
    }
}
