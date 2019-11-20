/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractResolveTest.java, Class: AbstractResolveTest
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

package com.ansorgit.plugins.bash.lang.psi.resolve;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.TestDataFile;
import junit.framework.AssertionFailedError;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class AbstractResolveTest extends LightBashCodeInsightFixtureTestCase {
    protected Project myProject;

    @Override
    public void tearDown() throws Exception {
        myProject = null;
        super.tearDown();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myProject = myFixture.getProject();
    }

    @Override
    protected String getBasePath() {
        return "/psi/resolve/resolving/";
    }

    @Nullable
    protected PsiReference configure() {
        String testName = getTestName(false) + ".bash";
        String data = loadTestDataFile(testName);
        int offset = data.indexOf("<ref>");
        if (offset == -1) {
            throw new IllegalStateException("<ref> marker not found");
        }

        String content = data.replaceFirst("<ref>", "");
        PsiFile file = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, content);
        PsiReference reference = file.getViewProvider().findReferenceAt(offset);
        if (reference == null) {
            throw new AssertionFailedError("no reference found in " + testName + " at offset " + offset);
        }
        return reference;
    }

    protected PsiFile addFile(@TestDataFile @NonNls String filePath) throws Exception {
        VirtualFile file = myFixture.copyFileToProject(filePath);

        // save and restore previously opened file
        PsiFile current = myFixture.getFile();

        myFixture.configureFromExistingVirtualFile(file);

        PsiFile newFile = myFixture.getFile();
        if (current != null) {
            myFixture.openFileInEditor(current.getVirtualFile());
        }

        return newFile;
    }

    public String loadTestDataFile(@TestDataFile String path) {
        try {
            return BashTestUtils.loadTestCaseFile(this, path);
        } catch (IOException e) {
            throw new RuntimeException("file not found: " + path);
        }
    }
}
