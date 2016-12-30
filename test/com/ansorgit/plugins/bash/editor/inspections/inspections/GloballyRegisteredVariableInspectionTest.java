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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import org.junit.Test;

public class GloballyRegisteredVariableInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        BashProjectSettings.storedSettings(getProject()).addGlobalVariable("GLOBAL_VAR");

        try {
            doTest("globallyRegisteredVariableInspection/ok", GloballyRegisteredVariableInspection.class, false);
        } finally {
            BashProjectSettings.storedSettings(getProject()).removeGlobalVariable("GLOBAL_VAR");
        }
    }

    @Test
    public void testWarning() throws Exception {
        doTest("globallyRegisteredVariableInspection/warning", GloballyRegisteredVariableInspection.class, false);
    }
}