package com.ansorgit.plugins.bash.editor.refactoring;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.google.common.collect.Lists;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * This test checks renaming of files and the related issues.
 *
 * @author jansorg
 */
public class FunctionRenameTest extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/editor/refactoring/FunctionRenameTestCase/";
    }

    /**
     * Tests the basic rename feature for references pointing to files.
     *
     */
    @Test
    public void testBasicRename() {
        doRename(false);
    }

    private void doRename(boolean renameWithHandler) {
        doRename(renameWithHandler, "source.bash");
    }

    private void doRename(final boolean renameWithHandler, String... sourceFiles) {
        doRename(new Runnable() {
            public void run() {
                if (renameWithHandler) {
                    myFixture.renameElementAtCaretUsingHandler("myFunction_renamed");
                } else {
                    myFixture.renameElementAtCaret("myFunction_renamed");
                }
            }
        }, sourceFiles);
    }

    private void doRename(Runnable renameLogic, String... sourceFiles) {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));

        List<String> filenames = Lists.newArrayList(sourceFiles);
        filenames.add("target.bash");
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
        Assert.assertTrue("Renamed reference wasn't found in the canonical text", psiReference.getCanonicalText().contains("myFunction_renamed"));

        PsiElement targetFile = psiReference.resolve();
        Assert.assertNotNull("target resolve result wasn't found", targetFile);
        Assert.assertTrue("target is not a psi function definition", targetFile instanceof BashFunctionDef);
    }
}
