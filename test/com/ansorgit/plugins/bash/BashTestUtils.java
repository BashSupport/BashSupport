/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashTestUtils.java, Class: BashTestUtils
 * Last modified: 2012-12-19
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

package com.ansorgit.plugins.bash;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.net.URL;

public final class BashTestUtils {

    private static volatile String basePath;

    private BashTestUtils() {
    }

    public static String getBasePath() {
        if (basePath == null) {
            basePath = computeBasePath();
        }

        if (basePath == null) {
            throw new IllegalStateException("Could not find the testData directory.");
        }

        //fixme not available in 135.x
        //VfsRootAccess.allowRootAccess(basePath);

        return basePath;
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

    public static PsiElement configureFixturePsiAtCaret(String fileNameInTestPath, CodeInsightTestFixture fixture) {
        fixture.configureByFile(fileNameInTestPath);

        PsiElement element = fixture.getFile().findElementAt(fixture.getCaretOffset());
        if (element instanceof LeafPsiElement) {
            return element.getParent();
        }

        return element;
    }
}
