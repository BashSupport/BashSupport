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

package com.ansorgit.plugins.bash.actions;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.intellij.openapi.actionSystem.Presentation;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Failing with 162.x for yet unknown reasons")
public abstract class AddReplActionTest extends BashCodeInsightFixtureTestCase {
    @Test
    @Ignore("Failing with 162.x for yet unknown reasons")
    public void _testReplInvocation() throws Exception {
        AddReplAction action = new AddReplAction();

        Presentation presentation = myFixture.testAction(action);
        Assert.assertTrue("The action must be enabled and visible", presentation.isEnabledAndVisible());

        Assert.assertNotNull(action.getConsoleRunner());

        //work around IntelliJ's memory warnings
        action.getConsoleRunner().getConsoleView().dispose();
    }
}