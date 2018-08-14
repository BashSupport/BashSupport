package com.ansorgit.plugins.bash.editor.codefolding;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.testFramework.TestLoggerFactory;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

public class BashFoldingTest extends LightPlatformCodeInsightFixtureTestCase {

  private void doTest() {
    myFixture.testFolding(getBasePath() + getTestName(false) + ".bash");
  }
  
  static {
    Logger.setFactory(TestLoggerFactory.class);
  }

  @NotNull
  public String getBasePath() {
    return BashTestUtils.getBasePath() + "/folding/";
  }

  public void testVariableFoldingWithCondition() {
    doTest();
  }
  
}