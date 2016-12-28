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

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.builders.EmptyModuleFixtureBuilder;
import com.intellij.testFramework.builders.ModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import org.jetbrains.annotations.NonNls;

import java.io.File;

/**
 * @author yole
 */
public abstract class BashCodeInsightFixtureTestCase<T extends ModuleFixtureBuilder> extends UsefulTestCase {
    protected CodeInsightTestFixture myFixture;
    protected Module myModule;

    protected PsiElement configurePsiAtCaret() {
        return configurePsiAtCaret(getTestName(true) + ".bash");
    }

    protected PsiElement configurePsiAtCaret(String fileNameInTestPath) {
        return BashTestUtils.configureFixturePsiAtCaret(fileNameInTestPath, myFixture);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        String name = getClass().getName() + "." + getName();
        final TestFixtureBuilder<IdeaProjectTestFixture> projectBuilder = IdeaTestFixtureFactory.getFixtureFactory().createFixtureBuilder(name);
        myFixture = IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(projectBuilder.getFixture());

        T moduleFixtureBuilder = projectBuilder.addModule(getModuleBuilderClass());
        tuneFixture(moduleFixtureBuilder);

        myFixture.setUp();
        myFixture.setTestDataPath(getTestDataPath());
        myModule = moduleFixtureBuilder.getFixture().getModule();
    }

    protected Class<T> getModuleBuilderClass() {
        return (Class<T>) EmptyModuleFixtureBuilder.class;
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

    protected void tuneFixture(final T moduleBuilder) {
        moduleBuilder.addSourceContentRoot(myFixture.getTempDirPath());
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

    protected boolean isCommunity() {
        return false;
    }

    protected Project getProject() {
        return myFixture.getProject();
    }

    protected Editor getEditor() {
        return myFixture.getEditor();
    }

    protected PsiFile getFile() {
        return myFixture.getFile();
    }
}
