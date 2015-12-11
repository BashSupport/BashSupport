package com.ansorgit.plugins.bash.lang.parser.eval;

import com.intellij.openapi.util.TextRange;
import org.junit.Test;

public class BashEnhancedTextPreprocessorTest extends AbstractTextPreprocessorTest {
    @Test
    public void testDecoding() throws Exception {
        assertDecoding("\\$abc\\t$abc\\n", "\\$abc\t$abc\n");
    }

    @Override
    protected TextPreprocessor createProcessor(String content) {
        return new BashEnhancedTextPreprocessor(TextRange.allOf(content));
    }
}