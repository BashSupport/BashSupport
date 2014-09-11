package com.ansorgit.plugins.bash.codeInsight.completion;

public class VariableNameLightCompletionProviderTest extends AbstractCompletionTest {
    public VariableNameLightCompletionProviderTest() {
        super("/codeInsight/completion/variableNameCompletion");
    }

    public void testSimpleCompletion() throws Exception {
        checkItems("abIsOk1", "abIsOk2");
    }

    public void testIncludedVariables() throws Exception {
        configureByTestName();

        checkItems("myVarIsOk", "myVarIsOk2", "myIncludedVarIsOk", "myIncludedVarIsOk2");
    }

    public void testDollarCompletion() throws Exception {
        checkItems("abIsOk", "aIsOk2");
    }
}
