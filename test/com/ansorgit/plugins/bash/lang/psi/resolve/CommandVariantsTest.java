/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: CommandVariantsTest.java, Class: CommandVariantsTest
 * Last modified: 2010-07-01
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

package com.ansorgit.plugins.bash.lang.psi.resolve;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.psi.PsiReference;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 30.06.2010
 * Time: 21:34:48
 */
public class CommandVariantsTest extends AbstractResolveTest {
    @Test
    public void testCommandVariants() throws Exception {
        checkVariants(configure(), 3);
    }

    @Test
    public void testCommandVariantsWithInner() throws Exception {
        checkVariants(configure(), 6);
    }

    private void checkVariants(PsiReference commandRef, int expectedVariantsCount) {
        BashCommand command = (BashCommand) commandRef.getElement();
        Assert.assertNotNull(command);

        Object[] variants = command.getVariants();

        int foundFunctionDef = 0;
        for (Object v : variants) {
            if (v instanceof BashFunctionDef) {
                foundFunctionDef++;
            }
        }

        Assert.assertEquals(expectedVariantsCount, foundFunctionDef);
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/resolve/commandVariants/";
    }
}
