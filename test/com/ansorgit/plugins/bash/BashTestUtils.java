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

package com.ansorgit.plugins.bash;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.LocalInspectionEP;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.testFramework.TestDataFile;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public final class BashTestUtils {

    private static volatile String basePath;

    private BashTestUtils() {
    }

    public static InspectionProfileEntry findInspectionProfileEntry(Class<? extends LocalInspectionTool> clazz) {
        LocalInspectionEP[] extensions = Extensions.getExtensions(LocalInspectionEP.LOCAL_INSPECTION);
        for (LocalInspectionEP extension : extensions) {
            if (extension.implementationClass.equals(clazz.getCanonicalName())) {
                extension.enabledByDefault = true;

                return extension.instantiateTool();
            }
        }

        throw new IllegalStateException("Unable to find inspection profile entry for " + clazz);
    }

    public static String getBasePath() {
        if (basePath == null) {
            basePath = computeBasePath();
        }

        if (basePath == null) {
            throw new IllegalStateException("Could not find the testData directory.");
        }

        VfsRootAccess.allowRootAccess(basePath);

        return basePath;
    }

    public static PsiElement configureFixturePsiAtCaret(String fileNameInTestPath, CodeInsightTestFixture fixture) {
        fixture.configureByFile(fileNameInTestPath);

        PsiElement element = fixture.getFile().findElementAt(fixture.getCaretOffset());
        if (element instanceof LeafPsiElement) {
            return element.getParent();
        }

        return element;
    }

    @NotNull
    public static String loadTestCaseFile(BashTestCase testCase, @TestDataFile String path) throws IOException {
        return FileUtil.loadFile(new File(testCase.getTestDataPath(), path.replace('/', File.separatorChar)), "UTF-8");
    }

    public static void assertPsiTreeByFile(BashTestCase testCase, PsiFile psiFile, String filePath) throws IOException {
        String actualPsi = DebugUtil.psiToString(psiFile, false).trim();
        String expectedPsi = loadTestCaseFile(testCase, filePath).trim();

        Assert.assertEquals(expectedPsi, actualPsi);
    }

    private static String computeBasePath() {
        String configuredDir = StringUtils.stripToNull(System.getenv("BASHSUPPORT_TESTDATA"));
        if (configuredDir != null) {
            File dir = new File(configuredDir);
            if (dir.isDirectory() && dir.exists()) {
                return dir.getAbsolutePath();
            }
        }

        //try to find out from the current classloader
        URL url = BashTestUtils.class.getClassLoader().getResource("log4j.xml");
        if (url != null) {
            try {
                File basePath = new File(url.toURI());
                while (basePath.exists() && !new File(basePath, "testData").isDirectory()) {
                    basePath = basePath.getParentFile();
                }

                //we need to cut the out dir and the other resource paths
                //File basePath = resourceFile.getParentFile().getParentFile().getParentFile().getParentFile();
                if (basePath.isDirectory()) {
                    return new File(basePath, "testData").getAbsolutePath();
                }
            } catch (Exception e) {
                //ignore, use fallback below
            }
        } else {
            throw new IllegalStateException("Could not find log4jx.ml");
        }

        return null;
    }
}
