package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.settings.BashProjectSettings;

/**
 * User: jansorg
 * Date: 09.02.11
 * Time: 22:07
 */
public class FunctionNameCompletionTest extends AbstractCompletionTest {
    @Override
    protected String getTestDir() {
        return "functionNameCompletion";
    }

    public void testSimpleCompletion() throws Exception {
        configure();

        checkItems("myFunctionOneIsOk", "myFunctionTwoIsOk", "myFunctionTwoOneIsOk");
    }

    public void testDollarCompletion() throws Exception {
        configure();

        checkItems(NO_COMPLETIONS);
    }

    public void testAutocompleteBuiltInDisabled() throws Exception {
        configure();
        checkItems("echo123");
    }

    public void testAutocompleteBuiltInEnabledCountOne() throws Exception {
        BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinCommands(true);

        configure();
        checkItems("echo123");
    }

    public void testAutocompleteBuiltInEnabledCountOneNoLocals() throws Exception {
        BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinCommands(true);

        configure();
        checkItems("echo");
    }

    public void testAutocompleteBuiltInEnabledCountTwo() throws Exception {
        BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinCommands(true);

        configure(2);
        checkItems("echo", "echo123");
    }

    public void testEmptyCompletion() throws Exception {
        configure();
        checkItems("myFunction");
    }

    public void testVarCompletion() throws Exception {
        configure();
        checkItems(NO_COMPLETIONS);
    }

    public void testParameterExpansion() throws Exception {
        configure();
        checkItems(NO_COMPLETIONS);
    }

    public void testVarDef() throws Exception {
        configure();
        checkItems(NO_COMPLETIONS);
    }

    public void testInnerFunctionCompletion() throws Exception {
        configure();
        checkItems(NO_COMPLETIONS);
    }

    public void testGlobalFunctionCompletion() throws Exception {
        configure();
        checkItems(NO_COMPLETIONS);
    }

    public void testNoNameCompletionInParam() throws Exception {
        configure();
        checkItems(NO_COMPLETIONS);
    }
}
