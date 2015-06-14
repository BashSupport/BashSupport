package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.google.common.collect.Lists;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringSettings;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This test checks renaming of files and the related issues.
 *
 * @author jansorg
 */
public class FileRenameTest extends LightCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/editor/refactoring/RenameTestCase/";
    }

    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + getBasePath();
    }

    /**
     * Tests the basic rename feature for references pointing to files.
     *
     * @throws Exception
     */
    @Test
    public void testBasicFileRename() throws Exception {
        doRename(false);
    }

    /**
     * Tests the basic rename feature for references pointing to files.
     *
     * @throws Exception
     */
    @Test
    public void testBasicFileRenameWithHandler() throws Exception {
        //this test is broken in 14.0.3 because the test framework doesn't pass the new name but uses a stupid default name instead, which is equal to the current name
        doRename(true);
    }

    /**
     * Tests renaming for references embedded in a double quoted string
     *
     * @throws Exception
     */
    @Test
    public void testBasicFileRenameDoubleQuoteFilename() throws Exception {
        doRename(false);
    }

    /**
     * Tests renaming for references embedded in a double quoted string
     *
     * @throws Exception
     */
    @Test
    public void testRenameBashCommandReference() throws Exception {
        doRename(false, "source.bash");
    }

    /**
     * Tests renaming for references embedded in a single quoted string
     *
     * @throws Exception
     */
    @Test
    public void testBasicFileRenameSingleQuoteFilename() throws Exception {
        boolean oldValue = RefactoringSettings.getInstance().RENAME_SEARCH_FOR_TEXT_FOR_FILE;
        RefactoringSettings.getInstance().RENAME_SEARCH_FOR_TEXT_FOR_FILE = true;
        try {
            doRename(false, "source.bash");
        } finally {
            RefactoringSettings.getInstance().RENAME_SEARCH_FOR_TEXT_FOR_FILE = oldValue;

        }
    }

    private void doRename(boolean renameWithHandler) {
        doRename(renameWithHandler, "source.bash", "source2.bash");
    }

    private void doRename(boolean renameWithHandler, String... sourceFiles) {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));

        ArrayList<String> filenames = Lists.newArrayList(sourceFiles);
        filenames.add("target.bash");
        myFixture.configureByFiles(filenames.toArray(new String[filenames.size()]));

        if (renameWithHandler) {
            myFixture.renameElementAtCaretUsingHandler("target_renamed.bash");
        } else {
            myFixture.renameElementAtCaret("target_renamed.bash");
        }

        for (String filename : sourceFiles) {
            myFixture.checkResultByFile(filename, FileUtil.getNameWithoutExtension(filename) + "_after." + FileUtilRt.getExtension(filename), false);
        }

        PsiElement psiElement = PsiTreeUtil.getParentOfType(myFixture.getFile().findElementAt(myFixture.getCaretOffset()), BashFileReference.class);
        Assert.assertNotNull("file reference is null", psiElement);
        Assert.assertTrue("Filename wasn't changed", psiElement.getText().contains("target_renamed.bash"));

        PsiReference psiReference = psiElement.getReference();
        Assert.assertNotNull("target file reference wasn't found", psiReference);
        Assert.assertTrue("Renamed reference wasn't found in the canonical text", psiReference.getCanonicalText().contains("target_renamed.bash"));

        PsiElement targetFile = psiReference.resolve();
        Assert.assertNotNull("target file resolve result wasn't found", targetFile);
        Assert.assertTrue("target is not a psi file", targetFile instanceof BashFile);
    }
}
