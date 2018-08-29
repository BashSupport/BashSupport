/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: FunctionNameCompletionTest.java, Class: FunctionNameCompletionTest
 * Last modified: 2013-02-03
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import org.junit.Test;

/**
 * @author jansorg
 */
public class FunctionNameCompletionTest extends AbstractCompletionTest {
    public FunctionNameCompletionTest() {
        super("/codeInsight/completion/functionNameCompletion");
    }

    @Test
    public void testSimpleCompletion() throws Exception {
        configureByTestName();

        checkItems("myFunctionOneIsOk", "myFunctionTwoIsOk", "myFunctionTwoOneIsOk");
    }

    @Test
    public void testDollarCompletion() throws Exception {
        configureByTestName();

        checkItems(NO_COMPLETIONS);
    }

    @Test
    public void testAutocompleteBuiltInDisabled() throws Exception {
        BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinCommands(false);
        configureByTestName();

        checkItems("echo123");
    }

    @Test
    public void testAutocompleteBuiltInEnabledCountOne() throws Exception {
        BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinCommands(true);
        configureByTestName();

        checkItems("echo123");
    }

    @Test
    public void testAutocompleteBuiltInEnabledCountOneNoLocals() throws Exception {
        BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinCommands(true);
        configureByTestName();

        checkItems("echo");
    }

    @Test
    public void testAutocompleteBuiltInEnabledCountTwo() throws Exception {
        BashProjectSettings.storedSettings(myProject).setAutocompleteBuiltinCommands(true);
        configureByTestName();

        //completes bash built-in command and the local function definition
        checkItemsCustomCompletion(2, "disown", "disown123");
    }

    @Test
    public void testEmptyCompletion() throws Exception {
        configureByTestName();

        //we expect the keywords for now
        //fixme smarter keyword completion is needed to offer them only in the right context
        checkItems("myFunction","case", "do", "done", "elif", "else", "esac", "false", "fi", "if", "in", "then", "true", "until", "while");
    }

    @Test
    public void testVarCompletion() throws Exception {
        configureByTestName();

        checkItems(NO_COMPLETIONS);
    }

    @Test
    public void testParameterExpansion() throws Exception {
        configureByTestName();

        checkItems(NO_COMPLETIONS);
    }

    @Test
    public void testVarDef() throws Exception {
        configureByTestName();

        checkItems(NO_COMPLETIONS);
    }

    @Test
    public void testInnerFunctionCompletion() throws Exception {
        configureByTestName();

        checkItems(NO_COMPLETIONS);
    }

    @Test
    public void testGlobalFunctionCompletion() throws Exception {
        configureByTestName();

        checkItems(NO_COMPLETIONS);
    }

    @Test
    public void testNoNameCompletionInParam() throws Exception {
        configureByTestName();

        checkItems(NO_COMPLETIONS);
    }
}
