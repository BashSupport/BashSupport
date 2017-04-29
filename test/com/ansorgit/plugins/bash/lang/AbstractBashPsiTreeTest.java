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

package com.ansorgit.plugins.bash.lang;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.TestDataFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 */
public abstract class AbstractBashPsiTreeTest extends LightBashCodeInsightFixtureTestCase {
    protected void assertPsiTree(@NotNull String content, @NotNull @TestDataFile String filePath) throws IOException {
        PsiFile psiFile = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, content);
        BashTestUtils.assertPsiTreeByFile(this, psiFile, filePath);
    }

    @Override
    protected String getBasePath() {
        return "psiTree";
    }
}
