package com.ansorgit.plugins.bash.lang.parser.eval;

import org.junit.Assert;

public abstract class AbstractTextPreprocessorTest {
    protected void assertDecoding(String content, String expected) {
        TextPreprocessor preprocessor = createProcessor(content);

        StringBuilder outChars = new StringBuilder();
        preprocessor.decode(content, outChars);

        Assert.assertEquals(expected, outChars.toString());
    }

    protected abstract TextPreprocessor createProcessor(String content);
}
