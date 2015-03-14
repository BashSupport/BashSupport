package com.ansorgit.plugins.bash.lang.psi.fileInclude;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.psi.PsiElement;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 18.02.11
 * Time: 20:25
 */
public class FileReferenceTest extends AbstractFileIncludeTest {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "fileReference/";
    }

    @Test
    public void testSimpleFileReference() throws Exception {
        PsiElement element = checkWithIncludeFile("includedFile.bash", true);
        Assert.assertTrue(element instanceof BashFile);
    }
}
