package com.ansorgit.plugins.bash.editor.refactoring;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.google.common.collect.Lists;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 * This test checks renaming of files and the related issues.
 *
 * @author jansorg
 */
public class RenameVariableTest extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/editor/refactoring/RenameVariableTestCase/";
    }

    @Test
    public void testBasicRename() throws Exception {
        doRename(false);
    }

    @Test
    public void testInjectedRename() throws Exception {
        doRename(false, "source.bash");
    }

    @Test
    public void testBasicRenameInlined() throws Exception {
        doRename(false);
    }

    @Test
    public void testEvalRename() throws Exception {
        doRename(false);
    }

    @Test
    public void testBasicEvalRename() throws Exception {
        doRename(false, "source.bash");
    }

    @Test
    public void testBasicEvalRenameInlined() throws Exception {
        doRename(false, "source.bash");
    }

    @Test
    @Ignore //broken atm
    public void _testEvalRenameInlined() throws Exception {
        doRename(new Runnable() {
            @Override
            public void run() {
                PsiReference psiReference = myFixture.getFile().findReferenceAt(myFixture.getEditor().getCaretModel().getOffset());
                Assert.assertTrue(psiReference.resolve() instanceof BashVarDef);

                myFixture.renameElementAtCaret("a_renamed");
            }
        }, "source.bash");
    }

    private void doRename(boolean renameWithHandler) {
        doRename(renameWithHandler, "source.bash", "target.bash");
    }

    private void doRename(final boolean renameWithHandler, String... sourceFiles) {
        doRename(new Runnable() {
            public void run() {
                if (renameWithHandler) {
                    myFixture.renameElementAtCaretUsingHandler("a_renamed");
                } else {
                    myFixture.renameElementAtCaret("a_renamed");
                }
            }
        }, sourceFiles);
    }

    private void doRename(Runnable renameLogic, String... sourceFiles) {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));

        List<String> filenames = Lists.newArrayList(sourceFiles);
        myFixture.configureByFiles(filenames.toArray(new String[filenames.size()]));

        renameLogic.run();

        for (String filename : filenames) {
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
        Assert.assertNotNull("target reference wasn't found", psiReference);
        Assert.assertTrue("Renamed reference wasn't found in the canonical text", psiReference.getCanonicalText().contains("a_renamed"));

        PsiElement targetVariable = psiReference.resolve();
        Assert.assertNotNull("target resolve result wasn't found", targetVariable);
        Assert.assertTrue("target is not a psi function definition", targetVariable instanceof BashVarDef);
    }
}
