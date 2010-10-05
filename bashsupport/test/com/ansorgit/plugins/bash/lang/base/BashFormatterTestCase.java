/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFormatterTestCase.java, Class: BashFormatterTestCase
 * Last modified: 2010-04-24
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

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import com.intellij.util.IncorrectOperationException;
import org.junit.Assert;

import java.io.IOException;

//this test is currently broken
public abstract class BashFormatterTestCase extends CodeInsightFixtureTestCase {
    private static final Logger LOG = Logger.getInstance("#BashFormatterTestCase");
    protected CodeStyleSettings myTempSettings;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setSettings(myFixture.getProject());
    }

    @Override
    protected void tearDown() throws Exception {
        setSettingsBack();
        super.tearDown();
    }

    protected void setSettings(Project project) {
        Assert.assertNull(myTempSettings);
        CodeStyleSettings settings = CodeStyleSettingsManager.getSettings(project);
        myTempSettings = settings.clone();

        CodeStyleSettings.IndentOptions gr = myTempSettings.getIndentOptions(BashFileType.BASH_FILE_TYPE);
        Assert.assertNotSame(gr, settings.OTHER_INDENT_OPTIONS);
        gr.INDENT_SIZE = 2;
        gr.CONTINUATION_INDENT_SIZE = 4;
        gr.TAB_SIZE = 2;
        myTempSettings.CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND = 3;

        CodeStyleSettingsManager.getInstance(project).setTemporarySettings(myTempSettings);
    }

    protected void setSettingsBack() {
        final CodeStyleSettingsManager manager = CodeStyleSettingsManager.getInstance(myFixture.getProject());
        myTempSettings.getIndentOptions(BashFileType.BASH_FILE_TYPE).INDENT_SIZE = 200;
        myTempSettings.getIndentOptions(BashFileType.BASH_FILE_TYPE).CONTINUATION_INDENT_SIZE = 200;
        myTempSettings.getIndentOptions(BashFileType.BASH_FILE_TYPE).TAB_SIZE = 200;

        myTempSettings.CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND = 5;
        manager.dropTemporarySettings();
        myTempSettings = null;
    }

    protected void checkFormatting(String fileText, String expected) throws Throwable {
        myFixture.configureByText(BashFileType.BASH_FILE_TYPE, fileText);
        checkFormatting(expected);
    }

    protected void checkFormatting(String expected) throws IOException {
        CommandProcessor.getInstance().executeCommand(myFixture.getProject(), new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        try {
                            final PsiFile file = myFixture.getFile();
                            TextRange myTextRange = file.getTextRange();
                            CodeStyleManager.getInstance(file.getProject()).reformatText(file, myTextRange.getStartOffset(), myTextRange.getEndOffset());
                        } catch (IncorrectOperationException e) {
                            LOG.error(e);
                        }
                    }
                });
            }
        }, null, null);
        myFixture.checkResult(expected);
    }
}
