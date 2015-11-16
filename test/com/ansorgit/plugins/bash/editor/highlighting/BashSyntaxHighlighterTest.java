package com.ansorgit.plugins.bash.editor.highlighting;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import org.junit.Test;

public class BashSyntaxHighlighterTest extends LightBashCodeInsightFixtureTestCase {

    @Override
    protected String getBasePath() {
        return "editor/highlighting/syntaxHighlighter";
    }

    @Override
    protected boolean isWriteActionRequired() {
        return false;
    }

    @Test
    public void testSimpleVariable() {
        doTestHighlighting();
    }

    @Test
    public void testBuiltinVariable() {
        doTestHighlighting();
    }

    protected void doTestHighlighting() {
        myFixture.testHighlighting(true, true, false, getTestDataPath() + "/" + getTestName(true) + ".bash");
    }
}