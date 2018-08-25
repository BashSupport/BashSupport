package com.ansorgit.plugins.bash.editor.codefolding;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

public class BashFoldingTest extends LightPlatformCodeInsightFixtureTestCase {

    private void doTest() {
        myFixture.testFolding(getBasePath() + getTestName(false) + ".bash");
    }

    public void testVariableFoldingWithCondition() {
        doTest();
    }

    public void testDifferentFunctions() {
        doTest();
    }

    public void testSameFunction() {
        doTest();
    }

    public void testTwoLevelVariableFolding() {
        doTest();
    }

    @NotNull
    public String getBasePath() {
        return BashTestUtils.getBasePath() + "/folding/";
    }

}