package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import org.junit.Ignore;
import org.junit.Test;

public class VariableNameCompletionProviderTest extends AbstractCompletionTest {
    public VariableNameCompletionProviderTest() {
        super("/codeInsight/completion/variableNameCompletion");
    }

    @Test
    public void testSimpleCompletion() throws Exception {
        configureByTestName();

        checkItems("abIsOk1", "abIsOk2");
    }

    @Test
    public void testGlobalCompletionInvocationOne() throws Exception {
        configureByTestName();

        boolean old = BashProjectSettings.storedSettings(myProject).isAutocompleteBuiltinVars();
        try {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(true);
            checkItemsCustomCompletion(1, "PWD_MINE");
        } finally {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(old);
        }
    }

    @Test
    public void testGlobalCompletionInvocationOneNoLocals() throws Exception {
        configureByTestName();

        boolean old = BashProjectSettings.storedSettings(myProject).isAutocompleteBuiltinVars();
        try {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(true);
            checkItemsCustomCompletion(1, "PWD", "COMP_WORDBREAKS", "COMP_WORDS", "OLDPWD");
        } finally {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(old);
        }
    }

    @Test
    public void testGlobalCompletionInvocationTwo() throws Exception {
        configureByTestName();

        boolean old = BashProjectSettings.storedSettings(myProject).isAutocompleteBuiltinVars();
        try {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(true);
            checkItemsCustomCompletion(2, "PWD_MINE", "PWD", "COMP_WORDBREAKS", "COMP_WORDS", "OLDPWD");
        } finally {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(old);
        }
    }

    @Test
    public void testSimpleParameterExpansion() throws Exception {
        configureByTestName();

        checkItems("abIsOk1", "abIsOk2");
    }

    @Test
    public void testWithinTrapCommand() throws Exception {
        //the trap command is a language injection host and contains a bash snippet
        configureByTestName();

        checkItems("inner", "outer");
    }

    @Test
    public void testWithinEvalCommand() throws Exception {
        //the eval command is a language injection host and contains a bash snippet
        configureByTestName();

        checkItems("inner", "outer");
    }

    @Test
    public void testEmptyParameterExpansion() throws Exception {
        configureByTestName();

        checkItems("abIsOk1", "abIsOk2");
    }

    @Test
    public void testParameterExpansionNoCommands() throws Exception {
        boolean oldAutocomplete = BashProjectSettings.storedSettings(myProject).isAutocompleteBuiltinCommands();

        try {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinCommands(true);

            configureByTestName();

            checkItems("echoVar");
        } finally {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinCommands(oldAutocomplete);
        }
    }

    //@Ignore("Completion inside comments seems to be IntelliJ's word completion")
    @Test
    public void testWithinComment() throws Exception {
        configureByTestName();

        checkItems(NO_COMPLETIONS);
    }

    @Test
    public void testIncludedVariables() throws Exception {
        configureByTestName(getBasePath() + "/include.bash");

        checkItems("myVarIsOk", "myVarIsOk2", "myIncludedVarIsOk", "myIncludedVarIsOk2");
    }

    @Test
    public void testIncludedVariablesEmpty() throws Exception {
        configureByTestName(getBasePath() + "/include.bash");

        checkItems("myVarIsOk", "myVarIsOk2", "includedVarHasOtherPrefix", "myIncludedVarIsOk", "myIncludedVarIsOk2");
    }

    @Test
    public void testDollarCompletion() throws Exception {
        configureByTestName();

        checkItems("abIsOk", "aIsOk2");
    }

    @Test
    public void testSelfReference() throws Exception {
        configureByTestName();

        checkItems();
    }
}
