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

package com.ansorgit.plugins.bash.lang.psiTree;

import com.ansorgit.plugins.bash.lang.AbstractBashPsiTreeTest;
import org.junit.Test;

public class VarDefTest extends AbstractBashPsiTreeTest {
    @Test
    public void testVarDef() throws Exception {
        assertPsiTree("foo=bar", "varDef/varDef.txt");
    }

    @Test
    public void testParseAssignmentList() throws Exception {
        assertPsiTree("foo=(${foo[@]%% (*})", "varDef/assignmentList.txt");
    }
}