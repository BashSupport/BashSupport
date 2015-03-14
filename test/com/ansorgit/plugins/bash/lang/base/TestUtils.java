/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: TestUtils.java, Class: TestUtils
 * Last modified: 2010-01-19
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

package com.ansorgit.plugins.bash.lang.base;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.LocalTimeCounter;
import org.jetbrains.annotations.NonNls;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class TestUtils {
    public static final String TEMP_FILE = "temp.bash";
    public static final String GSP_TEMP_FILE = "temp.bash";
    public static final String CARET_MARKER = "<caret>";
    public static final String BEGIN_MARKER = "<begin>";
    public static final String END_MARKER = "<end>";
    public static final String GROOVY_JAR = "groovy-all.jar";

    public static String getMockJdkHome() {
        return getTestDataPath() + "/mockJDK";
    }

    public static PsiFile createPseudoPhysicalBashFile(final Project project, final String text) throws IncorrectOperationException {
        return createPseudoPhysicalFile(project, TEMP_FILE, text);
    }


    public static PsiFile createPseudoPhysicalFile(final Project project, final String fileName, final String text) throws IncorrectOperationException {
        return PsiFileFactory.getInstance(project).createFileFromText(
                fileName,
                FileTypeManager.getInstance().getFileTypeByFileName(fileName),
                text,
                LocalTimeCounter.currentTime(),
                true);
    }

    private static
    @NonNls
    String TEST_DATA_PATH = null;

    public static String getTestDataPath() {
        if (TEST_DATA_PATH == null) {
            TEST_DATA_PATH = FileUtil.toSystemIndependentName(PathManager.getHomePath()) + "/svnPlugins/groovy/testdata";
        }
        return TEST_DATA_PATH;
    }

    public static String removeBeginMarker(String text) {
        int index = text.indexOf(BEGIN_MARKER);
        return text.substring(0, index) + text.substring(index + BEGIN_MARKER.length());
    }

    public static String removeEndMarker(String text) {
        int index = text.indexOf(END_MARKER);
        return text.substring(0, index) + text.substring(index + END_MARKER.length());
    }

    public static List<String> readInput(String filePath) throws IOException {
        String content = new String(FileUtil.loadFileText(new File(filePath)));
        Assert.assertNotNull(content);

        List<String> input = new ArrayList<String>();

        int separatorIndex;
        content = StringUtil.replace(content, "\r", ""); // for MACs

        // Adding input  before -----
        while ((separatorIndex = content.indexOf("-----")) >= 0) {
            input.add(content.substring(0, separatorIndex - 1));
            content = content.substring(separatorIndex);
            while (StringUtil.startsWithChar(content, '-')) {
                content = content.substring(1);
            }
            if (StringUtil.startsWithChar(content, '\n')) {
                content = content.substring(1);
            }
        }
        // Result - after -----
        if (content.endsWith("\n")) {
            content = content.substring(0, content.length() - 1);
        }
        input.add(content);

        Assert.assertTrue("No data found in source file", input.size() > 0);
        Assert.assertNotNull("Test output points to null", input.size() > 1);

        return input;
    }

    /**
     * Removes substring at pos index with length legth
     *
     * @param s        string
     * @param index    The start pos of substring to delete
     * @param length   length of substring to delete
     * @param wrappers Index wrappers
     * @return new String
     */
    public static String removeSubstring(final String s, final int index, final int length, IntRef... wrappers) {
        for (IntRef i : wrappers) {
            if (i.get() > index) {
                i.dec(length);
            }
        }
        return s.substring(0, index) + s.substring(index + length);
    }
}
