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

package com.ansorgit.plugins.bash.runner;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 */
public class BashLineErrorFilterTest extends BashCodeInsightFixtureTestCase {
    @Test
    public void testValidation() throws Exception {
        VirtualFile targetFile = ApplicationManager.getApplication().runWriteAction(new Computable<VirtualFile>() {
            @Override
            public VirtualFile compute() {
                try {
                    VirtualFile srcDir = myFixture.getTempDirFixture().findOrCreateDir("src");
                    return srcDir.createChildData(this, "test.sh");
                } catch (IOException e) {
                    return null;
                }
            }
        });

        PsiFile targetPsiFile = PsiManager.getInstance(getProject()).findFile(targetFile);
        Assert.assertNotNull(targetPsiFile);
        BashLineErrorFilter filter = new BashLineErrorFilter(getProject());

        String line = String.format("%s: line 13: notHere: command not found", targetFile.getCanonicalPath());
        Filter.Result result = filter.applyFilter(line, line.length());

        Assert.assertNotNull(result);

        HyperlinkInfo hyperlinkInfo = result.getFirstHyperlinkInfo();
        Assert.assertNotNull("Expected a hyperlink on the filename", hyperlinkInfo);

        hyperlinkInfo.navigate(getProject());

        VirtualFile[] openFiles = FileEditorManager.getInstance(getProject()).getOpenFiles();
        Assert.assertEquals("Expected just ony open file", 1, openFiles.length);
        Assert.assertEquals("Expected that navigation opened the target file", targetFile, openFiles[0]);
    }
}