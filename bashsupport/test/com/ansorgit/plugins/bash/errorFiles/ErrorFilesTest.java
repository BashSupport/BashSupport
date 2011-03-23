package com.ansorgit.plugins.bash.errorFiles;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.CodeInsightTestCase;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import org.junit.Assert;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 * User: jansorg
 * Date: 12.03.11
 * Time: 13:04
 */
public class ErrorFilesTest extends CodeInsightTestCase {
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/errorFiles/";
    }

    protected VirtualFile configure() throws Exception {
        return configureByFile(getTestName(false) + ".bash", null);
    }

    public void testAllFiles() throws Exception {
        List<File> files = FileUtil.findFilesByMask(Pattern.compile(".+\\.bash"), new File(getTestDataPath()));

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
            System.out.println(description(errors));
        }
        return count;
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
