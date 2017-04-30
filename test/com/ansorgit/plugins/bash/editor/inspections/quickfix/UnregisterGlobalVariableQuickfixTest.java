/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.editor.inspections.inspections.GloballyRegisteredVariableInspection;
import com.ansorgit.plugins.bash.editor.inspections.inspections.UnresolvedVariableInspection;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.undo.UndoManager;
import org.junit.Assert;

import java.util.List;

/**
 */
public class UnregisterGlobalVariableQuickfixTest extends BashCodeInsightFixtureTestCase {

    public void testUnregisterGlobalVar() throws Exception {
        myFixture.enableInspections(BashTestUtils.findInspectionProfileEntry(UnresolvedVariableInspection.class), BashTestUtils.findInspectionProfileEntry(GloballyRegisteredVariableInspection.class));
        configurePsiAtCaret("source.bash");

        BashProjectSettings.storedSettings(getProject()).addGlobalVariable("GLOBAL_VAR");
        try {
            List<IntentionAction> actions = myFixture.filterAvailableIntentions("Unregister as global variable");
            Assert.assertEquals(1, actions.size());

            IntentionAction action = actions.get(0);
            Assert.assertFalse(action.startInWriteAction());

            action.invoke(getProject(), getEditor(), getFile());

            Assert.assertFalse("The variable must be unregistered now", BashProjectSettings.storedSettings(getProject()).getGlobalVariables().contains("GLOBAL_VAR"));
            Assert.assertEquals("The action must be unavailable now", 0, myFixture.filterAvailableIntentions("Unregister as global variable").size());

            Assert.assertEquals("The register action must be available now", 1, myFixture.filterAvailableIntentions("Register as global variable").size());

            Assert.assertTrue("Undo must be available after the unregister action was executed", UndoManager.getInstance(getProject()).isUndoAvailable(null));

            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    UndoManager.getInstance(getProject()).undo(null);
                }
            });

            //now the unregister action must be available again
            Assert.assertEquals("The unregister action must be available again", 1, myFixture.filterAvailableIntentions("Unregister as global variable").size());
            Assert.assertEquals("The register action must be unavailable", 0, myFixture.filterAvailableIntentions("Register as global variable").size());
        } finally {
            BashProjectSettings.storedSettings(getProject()).removeGlobalVariable("GLOBAL_VAR");
        }
    }

    @Override
    protected String getBasePath() {
        return "/quickfixes/unregisterGlobalVarQuickfix/";
    }
}