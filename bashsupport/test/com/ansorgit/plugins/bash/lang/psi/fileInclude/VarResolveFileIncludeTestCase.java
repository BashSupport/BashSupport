package com.ansorgit.plugins.bash.lang.psi.fileInclude;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.junit.Assert;

/**
 * User: jansorg
 * Date: 06.02.11
 * Time: 12:57
 */
public class VarResolveFileIncludeTestCase extends AbstractFileIncludeTest {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "varResolve/";
    }

    public void testUnresolved1() throws Exception {
        assertUnresolved("includedFile.bash");
    }

    public void testSimpleResolve() throws Exception {
        checkWithIncludeFile("includedFile.bash", true);
    }

    public void testSimpleResolve2() throws Exception {
        checkWithIncludeFile("includedFile.bash", true);
    }

    public void testSimpleResolve3() throws Exception {
        checkWithIncludeFile("includedFile.bash", true);
    }

    public void testUnusedInclude() throws Exception {
        checkWithIncludeFile("includedFile.bash", false);
    }

    public void testUnusedInclude2() throws Exception {
        checkWithIncludeFile("includedFile.bash", false);
    }

    public void testRecursiveInclude() throws Exception {
        PsiReference reference = configure();
        Assert.assertNotNull(reference);

        //the var has to resolve to the definition in the included file
        PsiElement def = reference.resolve();
        Assert.assertNotNull("Variable is not properly resolved", def);
        Assert.assertTrue(reference.isReferenceTo(def));
    }

    public void testRecursiveLoopInclude() throws Exception {
        PsiReference reference = configure();
        Assert.assertNotNull(reference);

        PsiFile includeFile = addFile("includedFileLoop.bash");

        //the var has to resolve to the definition in the included file
        PsiElement def = reference.resolve();
        Assert.assertNotNull("Variable is not properly resolved", def);
        Assert.assertTrue(def.getContainingFile().equals(includeFile));
    }

    private void checkWithIncludeFile(String fileName, boolean assertDefInInclude) throws Exception {
        PsiReference reference = configure();
        Assert.assertNotNull(reference);

        PsiFile includeFile = addFile(fileName);

        //the var has to resolve to the definition in the included file
        PsiElement def = reference.resolve();
        Assert.assertNotNull("Variable is not properly resolved", def);

        boolean defIsInIncludeFile = def.getContainingFile().equals(includeFile);
        if (assertDefInInclude) {
            Assert.assertTrue("The variable is not defined in the include file.", defIsInIncludeFile);
        } else {
            Assert.assertFalse("The variable must not be defined in the include file.", defIsInIncludeFile);
        }
    }

    private void assertUnresolved(String includeFilePath) throws Exception {
        PsiReference reference = configure();
        Assert.assertNotNull(reference);

        PsiFile includeFile = addFile(includeFilePath);
        Assert.assertNotNull(includeFile);

        //the var has to resolve to the definition in the included file
        PsiElement def = reference.resolve();
        Assert.assertNull("Variable must not be resolved", def);
    }
}
