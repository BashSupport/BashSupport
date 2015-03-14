package com.ansorgit.plugins.bash.editor.annotator;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.intellij.codeInsight.daemon.LightDaemonAnalyzerTestCase;
import org.jetbrains.annotations.NotNull;

public class BashAnnotatorTest extends LightDaemonAnalyzerTestCase {
    @NotNull
    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath();
    }

    /**
     * https://code.google.com/p/bashsupport/issues/detail?id=192
     */
    public void testArithmeticHighlighingNPE() {
        configureByFile("/editor/annotator/arithmeticHighlighting.bash");
        doHighlighting();
    }
}