package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.completion.CompletionTestCase;
import org.jetbrains.annotations.NotNull;

abstract class AbstractCompletionTest extends CompletionTestCase {
    protected static final String[] NO_COMPLETIONS = new String[0];
    private final String basePath;
    private boolean oldBasic;
    private boolean oldSmart;

    public AbstractCompletionTest(String basePath) {
        this.basePath = basePath;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        oldBasic = CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_CODE_COMPLETION;
        oldSmart = CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_SMART_TYPE_COMPLETION;

        CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_CODE_COMPLETION = false;
        CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_SMART_TYPE_COMPLETION = false;

    }

    @Override
    protected void tearDown() throws Exception {
        CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_CODE_COMPLETION = oldBasic;
        CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_SMART_TYPE_COMPLETION = oldSmart;

        super.tearDown();
    }

    @NotNull
    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath();
    }

    @NotNull
    protected String getFullTestDataPath() {
        return BashTestUtils.getBasePath() + basePath;
    }

    protected void checkItemsCustomCompletion(int completions, String... values) throws Exception {
        boolean oldBasic = CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_CODE_COMPLETION;
        boolean oldSmart = CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_SMART_TYPE_COMPLETION;

        CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_CODE_COMPLETION = false;
        CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_SMART_TYPE_COMPLETION = false;

        try {
            configureByFileNoCompletion(basePath + "/" + getTestName(true) + ".bash");

            complete(completions);

            if (values == null || values.length == 0) {
                assertNull("No completion items were expected", myItems);
                return;
            }

            assertStringItems(values);
        } finally {
            CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_CODE_COMPLETION = oldBasic;
            CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_SMART_TYPE_COMPLETION = oldSmart;

        }
    }

    protected void configureByTestName() throws Exception {
        configureByFile(basePath + "/" + getTestName(true) + ".bash");
    }

    protected void checkItems(String... values) throws Exception {
        checkItemsCustomCompletion(1, values);
    }
}
