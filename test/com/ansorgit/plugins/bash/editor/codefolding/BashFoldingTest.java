package com.ansorgit.plugins.bash.editor.codefolding;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

public class BashFoldingTest extends LightPlatformCodeInsightFixtureTestCase {

    private void doTest() {
        try {
            BashProjectSettings.storedSettings(getProject()).setVariableFolding(true);
            myFixture.testFolding(getBasePath() + getTestName(false) + ".bash");
        } finally {
            BashProjectSettings.storedSettings(getProject()).setVariableFolding(false);
        }
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

    public void testLogicalBlock() {
        doTest();
    }

    public void testTwoLevelVariableFolding() {
        doTest();
    }

    public void testSubshell() {
        doTest();
    }

    @NotNull
    public String getBasePath() {
        return BashTestUtils.getBasePath() + "/folding/";
    }

}