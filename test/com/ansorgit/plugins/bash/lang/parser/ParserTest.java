/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ParserTest.java, Class: ParserTest
 * Last modified: 2010-01-27
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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.valueExpansion.PathUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.util.IncorrectOperationException;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public abstract class ParserTest extends TestCase {
    protected Project myProject;
    protected Module myModule;
    private static final String DATA_PATH = PathUtil.getDataPath(ParserTest.class) + "/testdata";

    protected IdeaProjectTestFixture myFixture;
    private static final String TEST_FILE_EXT = ".sh";

    protected void setUp() {
        myFixture = createFixture();

        try {
            myFixture.setUp();
        }
        catch (Exception e) {
            throw new Error(e);
        }
        myModule = myFixture.getModule();
        myProject = myModule.getProject();
    }

    protected IdeaProjectTestFixture createFixture() {
        TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = IdeaTestFixtureFactory.getFixtureFactory().createLightFixtureBuilder();
        return fixtureBuilder.getFixture();
    }

    protected void tearDown() {
        try {
            myFixture.tearDown();
        }
        catch (Exception e) {
            throw new Error(e);
        }
    }

    private PsiFile createPseudoPhysicalFile(final Project project, final String fileName, final String text) throws IncorrectOperationException {
        FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName);
        PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);

        return psiFileFactory.createFileFromText(
                fileName,
                fileType,
                text);
    }

    @Test
    public void testBashFileType() {
        Assert.assertNotNull(FileTypeManager.getInstance().getFileTypeByFileName("foo.bash"));
        Assert.assertNotNull(FileTypeManager.getInstance().getFileTypeByFileName("foo.sh"));
    }

    private void parseit(File file) {
        Assert.assertTrue(file.exists());

        System.out.println("Parsing file " + file.getAbsolutePath());

        StringBuilder contents = new StringBuilder();
        try {
            BufferedReader input = new BufferedReader(new FileReader(file));
            try {
                String line = null;
                if ((line = input.readLine()) != null) {
                    contents.append(line);
                }
                while ((line = input.readLine()) != null) {
                    contents.append(System.getProperty("line.separator"));
                    contents.append(line);
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        PsiFile psiFile = createPseudoPhysicalFile(myProject, "test.clj", contents.toString());
        String psiTree = DebugUtil.psiToString(psiFile, false);
        System.out.println(psiTree);
    }

    @Test
    public void testAllFiles() {
        File dir = new File(DATA_PATH);
        for (File file : dir.listFiles()) {
            parseit(file);
        }
    }
}
