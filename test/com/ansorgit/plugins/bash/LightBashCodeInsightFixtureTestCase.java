/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ansorgit.plugins.bash;

import com.intellij.lang.Language;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PlatformTestCase;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import org.jetbrains.annotations.NonNls;

import java.io.File;

/**
 * Code taken from IntelliJ.
 * <p/>
 * We need to fix the implementation of the fixture to fix myFixture.testHighlighting.
 *
 * @author peter
 */
@SuppressWarnings("JUnitTestCaseWithNonTrivialConstructors")
public abstract class LightBashCodeInsightFixtureTestCase extends UsefulTestCase {
    protected CodeInsightTestFixture myFixture;
    protected Module myModule;

    public LightBashCodeInsightFixtureTestCase() {
        this(true);
    }

    protected LightBashCodeInsightFixtureTestCase(boolean autodetect) {
        if (autodetect) {
            PlatformTestCase.autodetectPlatformPrefix();
        }
    }

    /**
     * Return relative path to the test data. Path is relative to the
     * {@link com.intellij.openapi.application.PathManager#getHomePath()}
     *
     * @return relative path to the test data.
     */
    @NonNls
    protected String getBasePath() {
        return "";
    }

    /**
     * Return absolute path to the test data. Not intended to be overridden.
     *
     * @return absolute path to the test data.
     */
    @NonNls
    protected String getTestDataPath() {
        String basePath = getBasePath();
        return BashTestUtils.getBasePath() + (basePath.endsWith(File.separator) ? "" : File.separator) + basePath;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        IdeaTestFixtureFactory factory = IdeaTestFixtureFactory.getFixtureFactory();

        TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = factory.createLightFixtureBuilder(getProjectDescriptor());
        IdeaProjectTestFixture fixture = fixtureBuilder.getFixture();

        myFixture = createCodeInsightFixture(fixture);

        myFixture.setUp();
        myFixture.setTestDataPath(getTestDataPath());

        myModule = myFixture.getModule();
    }

    protected CodeInsightTestFixture createCodeInsightFixture(IdeaProjectTestFixture projectFixture) {
        //return new BashCodeInsightTestFixtureImpl(projectFixture, new TempDirTestFixtureImpl());
        return IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(projectFixture);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            myFixture.tearDown();
        } finally {
            myFixture = null;
            myModule = null;

            super.tearDown();
        }
    }

    protected LightProjectDescriptor getProjectDescriptor() {
        return null;
    }

    protected boolean isCommunity() {
        return false;
    }

    @Override
    protected void runTest() throws Throwable {
        if (isWriteActionRequired()) {
            new WriteCommandAction(getProject()) {
                @Override
                protected void run(Result result) throws Throwable {
                    doRunTests();
                }
            }.execute();
        } else {
            doRunTests();
        }
    }

    protected boolean isWriteActionRequired() {
        return true;
    }

    protected void doRunTests() throws Throwable {
        LightBashCodeInsightFixtureTestCase.super.runTest();
    }

    protected Project getProject() {
        return myFixture.getProject();
    }

    protected PsiManager getPsiManager() {
        return PsiManager.getInstance(getProject());
    }

    protected PsiFile createLightFile(final FileType fileType, final String text) {
        return PsiFileFactory.getInstance(getProject()).createFileFromText("a." + fileType.getDefaultExtension(), fileType, text);
    }

    public PsiFile createLightFile(final String fileName, final Language language, final String text) {
        return PsiFileFactory.getInstance(getProject()).createFileFromText(fileName, language, text, false, true);
    }

}