package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.intellij.codeInsight.completion.LightCompletionTestCase;
import org.jetbrains.annotations.NotNull;

abstract class AbstractLightCompletionTest extends LightCompletionTestCase {
    private final String basePath;

    public AbstractLightCompletionTest(String basePath) {
        this.basePath = basePath;
    }

    @NotNull
    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath();
    }

    protected void checkItems(String... values) throws Exception {
        configureByFile(basePath + "/" + getTestName(true) + ".bash");

        testByCount(values.length, values);
    }
}
