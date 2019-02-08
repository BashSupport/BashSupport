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

public class VarTest extends AbstractBashPsiTreeTest {
    @Test
    public void testVar() throws Exception {
        assertPsiTree("$foo", "var.txt");
    }

    @Test
    public void testPrintfVar() throws Exception {
        assertPsiTree("printf -v foo 'test'", "printfVar.txt");
    }

    @Test
    public void testPrintfInterpolationStringVar() throws Exception {
        assertPsiTree("printf -v \"${foo}\" 'test'", "printfInterpolationStringVar.txt");
    }

    @Test
    public void testPrintfStringVar() throws Exception {
        assertPsiTree("printf -v \"foo\" 'test'", "printfStringVar.txt");
    }

    @Test
    public void testPrintfString2Var() throws Exception {
        assertPsiTree("printf -v \'foo\' 'test'", "printfString2Var.txt");
    }

    @Test
    public void testPrintfVarConcatenated() throws Exception {
        // bash treats "foo""bar" as a single variable name "foobar", we can't support that at the moment
        // BashSupport must not parse this into two separate variable definitions
        assertPsiTree("printf -v \"foo\"\"bar\" 'test'", "printfStringConcatenated.txt");
    }

    @Override
    protected String getBasePath() {
        return "psiTree/var";
    }
}