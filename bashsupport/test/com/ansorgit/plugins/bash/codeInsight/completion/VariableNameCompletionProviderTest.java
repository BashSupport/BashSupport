package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.settings.BashProjectSettings;

public class VariableNameCompletionProviderTest extends AbstractCompletionTest {
    public VariableNameCompletionProviderTest() {
        super("/codeInsight/completion/variableNameCompletion");
    }

    public void testSimpleCompletion() throws Exception {
        checkItems("abIsOk1", "abIsOk2");
    }

    public void testGlobalCompletionInvocationOne() throws Exception {
        boolean old = BashProjectSettings.storedSettings(myProject).isAutocompleteBuiltinVars();
        try {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(true);

            configureByTestName();

            checkItemsCustomCompletion(1, "PWD_MINE");
        } finally {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(old);
        }
    }

    public void testGlobalCompletionInvocationOneNoLocals() throws Exception {
        boolean old = BashProjectSettings.storedSettings(myProject).isAutocompleteBuiltinVars();
        try {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(true);

            configureByTestName();

            checkItemsCustomCompletion(1, "PWD", "COMP_WORDBREAKS", "COMP_WORDS", "OLDPWD");
        } finally {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(old);
        }
    }

    public void testGlobalCompletionInvocationTwo() throws Exception {
        boolean old = BashProjectSettings.storedSettings(myProject).isAutocompleteBuiltinVars();
        try {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(true);

            configureByTestName();

            checkItemsCustomCompletion(2, "PWD_MINE", "PWD", "COMP_WORDBREAKS", "COMP_WORDS", "OLDPWD");
        } finally {
            BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinVars(old);
        }
    }


    public void testSimpleParameterExpansion() throws Exception {
        configureByTestName();

        checkItems("abIsOk1", "abIsOk2");
    }

    public void testWithinTrapCommand() throws Exception {
        //the trap command is a language injection host and contains a bash snippet
        configureByTestName();

        checkItems("inner", "outer");
    }

    public void testEmptyParameterExpansion() throws Exception {
        checkItems("abIsOk1", "abIsOk2");
    }

    public void testParameterExpansionNoCommands() throws Exception {
        BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinCommands(true);

        configureByTestName();

        checkItems("echoVar");
    }

    /*@Ignore("Completion inside comments seems to be IntelliJ's word completion")
    public void testWithinComment() throws Exception {
        configure();
        checkItems(NO_COMPLETIONS);
    } */


    public void testSelfReference() throws Exception {
        configureByTestName();

        checkItems();
    }
}
