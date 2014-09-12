package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.completion.CompletionTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

abstract class AbstractCompletionTest extends CompletionTestCase {
    protected static final String[] NO_COMPLETIONS = new String[0];
    private final String basePath;
    private final boolean autoInsertionEnabled;

    private boolean oldBasic;
    private boolean oldSmart;

    public AbstractCompletionTest(String basePath) {
        this(basePath, false);
    }

    public AbstractCompletionTest(String basePath, boolean autoInsertionEnabled) {
        this.basePath = basePath;
        this.autoInsertionEnabled = autoInsertionEnabled;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        oldBasic = CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_CODE_COMPLETION;
        oldSmart = CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_SMART_TYPE_COMPLETION;

        CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_CODE_COMPLETION = autoInsertionEnabled;
        CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_SMART_TYPE_COMPLETION = autoInsertionEnabled;

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

    protected void configureByTestName(String... additionalFiles) throws Exception {
        String testnameFile = basePath + "/" + getTestName(true) + ".bash";

        ArrayList<String> files = Lists.newArrayList(testnameFile);
        if (additionalFiles != null && additionalFiles.length > 0) {
            files.addAll(Arrays.asList(additionalFiles));
        }

        configureByFiles(null, files.toArray(new String[files.size()]));
    }

    protected void checkItemsCustomCompletion(int completions, String... values) throws Exception {
        complete(completions);

        if (values == null || values.length == 0) {
            assertNull("No completion items were expected", myItems);
            return;
        }

        assertStringItems(values);
    }

    protected void checkItems(String... values) throws Exception {
        checkItemsCustomCompletion(1, values);
    }

    public String getBasePath() {
        return basePath;
    }
}
