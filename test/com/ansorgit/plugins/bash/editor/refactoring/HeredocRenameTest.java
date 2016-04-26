package com.ansorgit.plugins.bash.editor.refactoring;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocMarker;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for heredoc marker rename refactorings.
 *
 * @author jansorg
 */
public class HeredocRenameTest extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/editor/refactoring/HeredocTestCase/";
    }

    @Test
    public void testBasicMarkerRename() throws Exception {
        doRename(false);
    }

    @Test
    public void testDollarMarkerRename() throws Exception {
        doRename(new Runnable() {
            public void run() {
                myFixture.renameElementAtCaret("$_RENAMED");
            }
        }, "$_RENAMED", "source.bash");
    }

    @Test
    public void testEndMarkerRename() throws Exception {
        doRename(false);
    }

    @Test
    public void testNestedHeredocRename() throws Exception {
        doRename(false);
    }

    @Test
    public void testWrappedMarkerRename() throws Exception {
        doRename(false);
    }

    @Test
    public void testEscapedMarkerRename() throws Exception {
        doRename(false);
    }

    @Test
    public void testWrappedPrefixedMarkerRename() throws Exception {
        doRename(false);
    }

    @Test
    public void testEscapedVariableRename() throws Exception {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));
        myFixture.configureByFiles("source.bash");

        myFixture.renameElementAtCaret("X_RENAMED");

        myFixture.checkResultByFile("source.bash", "source_after.bash", false);
    }

    private void doRename(boolean renameWithHandler) {
        doRename(renameWithHandler, "source.bash");
    }

    private void doRename(final boolean renameWithHandler, String... sourceFiles) {
        doRename(new Runnable() {
            public void run() {
                if (renameWithHandler) {
                    myFixture.renameElementAtCaretUsingHandler("EOF_RENAMED");
                } else {
                    myFixture.renameElementAtCaret("EOF_RENAMED");
                }
            }
        }, "EOF_RENAMED", sourceFiles);
    }

    private void doRename(Runnable renameLogic, String newName, String... sourceFiles) {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));
        myFixture.configureByFiles(sourceFiles);

        renameLogic.run();

        for (String filename : sourceFiles) {
            myFixture.checkResultByFile(filename, FileUtil.getNameWithoutExtension(filename) + "_after." + FileUtilRt.getExtension(filename), false);
        }

        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        Assert.assertNotNull("caret element is null", psiElement);

        while (psiElement.getReference() == null) {
            if (psiElement.getParent() == null) {
                break;
            }

            psiElement = psiElement.getParent();
        }

        PsiReference psiReference = psiElement.getReference();
        Assert.assertNotNull("target file reference wasn't found", psiReference);
        Assert.assertTrue("Renamed reference wasn't found in the canonical text: " + psiReference.getCanonicalText(), psiReference.getCanonicalText().contains(newName));

        PsiElement targetMarker = psiReference.resolve();
        Assert.assertNotNull("target file resolve result wasn't found", targetMarker);
        Assert.assertTrue("target is not a psi file", targetMarker instanceof BashHereDocMarker);
    }
}
