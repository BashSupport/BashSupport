/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractArithExprTest.java, Class: AbstractArithExprTest
 * Last modified: 2010-07-17
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

package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.intellij.codeInsight.CodeInsightTestCase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public abstract class AbstractShellCommandTest extends CodeInsightTestCase {
    protected PsiElement configureCommand() throws Exception {
        configure();
        setupCursorAndSelection(myEditor);

        PsiFile psi = getFile();

        return psi.findElementAt(getEditor().getCaretModel().getOffset());
    }

    protected VirtualFile configure() throws Exception {
        return configureByFile(getTestName(true) + ".bash", "");
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/shell/";
    }
}
