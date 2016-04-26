package com.ansorgit.plugins.bash.lang.psi.fileInclude;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jansorg
 */
public class VarResolveFileIncludeTestCase extends AbstractFileIncludeTest {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "varResolve/";
    }

    @Test
    public void testUnresolved1() throws Exception {
        assertUnresolved("includedFile.bash");
    }

    @Test
    public void testSimpleResolve() throws Exception {
        checkWithIncludeFile("includedFile.bash", true);
    }

    @Test
    public void testSimpleResolve2() throws Exception {
        checkWithIncludeFile("includedFile.bash", true);
    }

    @Test
    public void testSimpleResolve3() throws Exception {
        checkWithIncludeFile("includedFile.bash", true);
    }

    @Test
    public void testUnusedInclude() throws Exception {
        checkWithIncludeFile("includedFile.bash", false);
    }

    @Test
    public void testUnusedInclude2() throws Exception {
        checkWithIncludeFile("includedFile.bash", false);
    }

    @Test
    public void testRecursiveInclude() throws Exception {
        PsiReference reference = configure();
        Assert.assertNotNull(reference);

        //the var has to resolve to the definition in the included file
        PsiElement def = reference.resolve();
        Assert.assertNotNull("Variable is not properly resolved", def);
        Assert.assertTrue(reference.isReferenceTo(def));
    }

    @Test
    public void testRecursiveLoopInclude() throws Exception {
        PsiReference reference = configure();
        Assert.assertNotNull(reference);

        PsiFile includeFile = addFile("includedFileLoop.bash");

        //the var has to resolve to the definition in the included file
        PsiElement def = reference.resolve();
        Assert.assertNotNull("Variable is not properly resolved", def);
        Assert.assertTrue(def.getContainingFile().equals(includeFile));
    }
}
