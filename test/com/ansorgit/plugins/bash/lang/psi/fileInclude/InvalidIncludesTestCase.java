package com.ansorgit.plugins.bash.lang.psi.fileInclude;

import com.ansorgit.plugins.bash.lang.psi.FileInclusionManager;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * @author jansorg
 */
public class InvalidIncludesTestCase extends AbstractFileIncludeTest {
    @Override
    protected String getBasePath() {
        return "psi/fileInclude/invalidIncludes/";
    }

    @Test
    public void testTextIncluded() throws Exception {
        Set<PsiFile> includedFiles = findIncludedFiles(false);

        Assert.assertEquals("Included text file was not found!", 1, includedFiles.size());
        Assert.assertFalse("Included file shouldn't be a Bash file", includedFiles.iterator().next() instanceof BashFile);
    }

    @Test
    public void testTextIncludedJustBash() throws Exception {
        Set<PsiFile> includedFiles = findIncludedFiles(true);
        Assert.assertEquals("Included text file was not found!", 0, includedFiles.size());
    }

    private Set<PsiFile> findIncludedFiles(boolean justBashFiles) throws Exception {
        PsiReference ref = configure();
        addFile("included.txt");

        Assert.assertNotNull("Reference was not found", ref);

        PsiFile file = ref.getElement().getContainingFile();
        Assert.assertTrue(file instanceof BashFile);

        return FileInclusionManager.findIncludedFiles(file, true, justBashFiles);
    }
}
