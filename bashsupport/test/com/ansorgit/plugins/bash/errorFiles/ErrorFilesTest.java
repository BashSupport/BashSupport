package com.ansorgit.plugins.bash.errorFiles;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.CodeInsightTestCase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import org.junit.Assert;

import java.util.List;

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

    //supers.bash triggered an empty stack exception
    public void testSupers() throws Exception {
        configure();

        assertNoParsingErrors();
    }

    //supers.bash triggered an empty stack exception
    public void testSupersSmall() throws Exception {
        configure();

        assertNoParsingErrors();
    }

    private void assertNoParsingErrors() {
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
        Assert.assertEquals("There should be no errors in the file. Found " + count + " errors: " + description(errors), 0, errors.size());
    }

    private String description(List<PsiErrorElement> errors) {
        StringBuilder builder = new StringBuilder();

        for (PsiErrorElement error : errors) {
            builder.append("\n\t").append(error.getErrorDescription());
            builder.append(": '").append(error.getText()).append("'");
        }

        builder.append("\n");
        return builder.toString();
    }
}
