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

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashBackquote;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class BashBackquoteImplTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testCommandText() throws Exception {
        PsiFile file = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, "echo `echo hi`");
        BashBackquote backquote = PsiTreeUtil.findChildOfType(file, BashBackquote.class, false);

        Assert.assertNotNull(backquote);

        Assert.assertEquals("echo hi", backquote.getCommandText());
    }
}