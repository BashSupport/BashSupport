package com.ansorgit.plugins.bash.lang.psi.fileInclude;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import org.junit.Assert;

/**
 * User: jansorg
 * Date: 06.02.11
 * Time: 12:57
 */
public class ResolveScopeTestCase extends AbstractFileIncludeTest {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "resolveScope/";
    }

    public void testSimpleScope() throws Exception {
        PsiReference reference = configure();
        Assert.assertNotNull(reference);

        PsiFile includeFile = addFile("include.bash");
        PsiFile include2File = addFile("include2.bash");

        //the var has to resolve to the definition in the included file
        PsiElement def = reference.resolve();
        Assert.assertNotNull("Variable is not properly resolved", def);

        boolean defIsInIncludeFile = def.getContainingFile().equals(includeFile);
        if (true) {
            Assert.assertTrue("The variable is not defined in the include file.", defIsInIncludeFile);
        } else {
            Assert.assertFalse("The variable must not be defined in the include file.", defIsInIncludeFile);
        }

        Assert.assertFalse("Resolve returned invalid containing file.", def.getContainingFile().equals(myFile));

        //the search scope must contain all the included files but nothing else
        GlobalSearchScope resolveScope = reference.getElement().getResolveScope();
        Assert.assertTrue("A file must be in its own resolve scope", resolveScope.contains(myFile.getVirtualFile()));
        Assert.assertTrue("An included file must be in the resolve scope", resolveScope.contains(def.getContainingFile().getVirtualFile()));

        //must not be included
        Assert.assertFalse("An excluded file must not be in the resolve scope", resolveScope.contains(include2File.getVirtualFile()));
    }

    public void testTwoStepScope() throws Exception {
        testTwoStepInclusion("include.bash", "include2.bash");
    }

    public void testRecursiveStepScope() throws Exception {
        testTwoStepInclusion("includeRecursive.bash", "includeRecursive2.bash");
    }

    private void testTwoStepInclusion(String firstFile, String secondFile) throws Exception {
        PsiReference reference = configure();
        Assert.assertNotNull(reference);

        PsiFile includeFile = addFile(firstFile);
        PsiFile include2File = addFile(secondFile);

        //the var has to resolve to the definition in the included file
        PsiElement varDef = reference.resolve();
        Assert.assertNotNull("Variable is not properly resolved", varDef);

        boolean defIsInIncludeFile = varDef.getContainingFile().equals(include2File);
        Assert.assertTrue("The variable must be defined in the include file.", defIsInIncludeFile);

        GlobalSearchScope resolveScope = reference.getElement().getResolveScope();
        Assert.assertTrue("A file must be in its own resolve scope", resolveScope.contains(myFile.getVirtualFile()));

        Assert.assertTrue("The variable must be resolved to the definition in 'include2.bash'", include2File.equals(varDef.getContainingFile()));

        Assert.assertTrue("An included file must be in the resolve scope", resolveScope.contains(varDef.getContainingFile().getVirtualFile()));
        Assert.assertTrue("The included file must be in the resolve scope", resolveScope.contains(include2File.getVirtualFile()));
        Assert.assertTrue("The included file (second inclusion step) must be in the resolve scope", resolveScope.contains(includeFile.getVirtualFile()));
    }
}
