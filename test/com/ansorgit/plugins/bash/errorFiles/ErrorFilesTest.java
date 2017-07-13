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

package com.ansorgit.plugins.bash.errorFiles;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.CodeInsightTestCase;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author jansorg
 */
public class ErrorFilesTest extends CodeInsightTestCase {
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/errorFiles/";
    }

    protected VirtualFile configure() throws Exception {
        return configureByFile(getTestName(false) + ".bash", null);
    }

    @Test
    public void testAllFiles() throws Exception {
        assertNoErrors(".+\\.bash");
    }

    @Test
    public void testBinaryFileIssue420() throws Exception {
        assertNoErrors("420-binaryData\\.bash");
    }

    private int assertNoParsingErrors() {
        final List<PsiErrorElement> errors = Lists.newLinkedList();

        PsiFile file = getFile();
        Assert.assertNotNull("File not found", file);

        BashPsiUtils.visitRecursively(file, new BashVisitor() {
            @Override
            public void visitErrorElement(PsiErrorElement element) {
                errors.add(element);
            }
        });

        int count = errors.size();
        if (count > 0) {
            System.err.println(description(errors));
        }
        return count;
    }

    private void assertNoErrors(String filenameRegex) throws Exception {
        try {
            BashProjectSettings.storedSettings(getProject()).setEvalEscapesEnabled(true);

            List<File> files = FileUtil.findFilesByMask(Pattern.compile(filenameRegex), new File(getTestDataPath()));

            int count = 0;
            int errors = 0;

            for (File file : files) {
                LOG.info("Checking file: " + file.getAbsolutePath());
                configureByFile(file.getName(), null);
                errors += assertNoParsingErrors();

                count++;
            }

            Assert.assertTrue("No files parsed.", count > 0);
            Assert.assertTrue("There are " + errors + " errors", errors == 0);
        } finally {
            BashProjectSettings.storedSettings(getProject()).setEvalEscapesEnabled(false);
        }
    }

    private String description(List<PsiErrorElement> errors) {
        StringBuilder builder = new StringBuilder();

        builder.append("\n## File: " + getFile().getName());
        builder.append(", Errors: " + errors.size());
        for (PsiErrorElement error : errors) {
            builder.append("\n\t").append(error.getErrorDescription());
            builder.append(": '").append(error.getText()).append("'").append(", line ").append(BashPsiUtils.getElementLineNumber(error));
            //builder.append(", column ").append(error.getTgetTextOffset());
        }

        builder.append("\n\n");
        return builder.toString();
    }
}
