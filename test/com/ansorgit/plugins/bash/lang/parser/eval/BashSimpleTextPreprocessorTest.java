package com.ansorgit.plugins.bash.lang.parser.eval;

import com.intellij.openapi.util.TextRange;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("Duplicates")
public class BashSimpleTextPreprocessorTest {
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
        assertDecoding("\\$1=\\$(echo)", "\\$1=$(echo)");
    }

    @Test
    public void testDecoding() throws Exception {
        assertDecoding("eval printf '%s\\\\n' \"\\$$varname\"", "eval printf '%s\\n' \"\\$$varname\"");

    }

    private void assertDecoding(String content, String expected) {
        BashSimpleTextPreprocessor preprocessor = new BashSimpleTextPreprocessor(TextRange.allOf(content));

        StringBuilder outChars = new StringBuilder();
        preprocessor.decode(content, outChars);

        Assert.assertEquals(expected, outChars.toString());
    }
}