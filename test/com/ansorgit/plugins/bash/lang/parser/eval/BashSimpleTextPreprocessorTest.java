package com.ansorgit.plugins.bash.lang.parser.eval;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("Duplicates")
public class BashSimpleTextPreprocessorTest extends AbstractTextPreprocessorTest {
    @Test
    public void testSimpleDecoding() throws Exception {
        String content = "$pathVar=$pathAddition";
        BashSimpleTextPreprocessor preprocessor = new BashSimpleTextPreprocessor(TextRange.allOf(content));

        StringBuilder outChars = new StringBuilder();
        preprocessor.decode(content, outChars);

        Assert.assertEquals("$pathVar=$pathAddition", outChars.toString());

        for (int i = 0; i < content.length(); i++) {
            Assert.assertTrue(i == preprocessor.getOffsetInHost(i));
        }
    }

    @Test
    public void testSubshellDecoding() throws Exception {
        assertDecoding("$1=\\$(echo)", "$1=$(echo)");
        assertDecoding("\\$1=\\$(echo)", "$1=$(echo)");
    }

    @Test
    public void testDecoding() throws Exception {
        assertDecoding("eval printf '%s\\\\n' \"\\$$varname\"", "eval printf '%s\\n' \"\\$$varname\"");
    }

    @NotNull
    protected BashSimpleTextPreprocessor createProcessor(String content) {
        return new BashSimpleTextPreprocessor(TextRange.allOf(content));
    }
}