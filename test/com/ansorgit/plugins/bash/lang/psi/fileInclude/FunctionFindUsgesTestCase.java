package com.ansorgit.plugins.bash.lang.psi.fileInclude;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.intellij.psi.PsiReference;
import com.intellij.usageView.UsageInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class FunctionFindUsgesTestCase extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "psi/usageSearch/function/";
    }

    @Test
    public void testFindUsages1() throws Exception {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));

        assertUsages(new String[]{"source.bash", "includedFile.bash"});
    }

    @Test
    public void testFindUsages2() throws Exception {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));

        assertUsages(new String[]{"includedFile.bash", "source.bash"});

    }

    @Test
    public void testFindUsages3() throws Exception {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));

        assertUsages(new String[]{"subdir/includedFile.bash", "subdir/source.bash"});
    }

    private void assertUsages(String[] files) {
        PsiReference reference = myFixture.getReferenceAtCaretPosition(files);
        Assert.assertNotNull("Unable to find the reference", reference);
        Assert.assertNotNull("Unable to resolve reference", reference.resolve());

        Collection<UsageInfo> usages = myFixture.findUsages(reference.resolve());
        Assert.assertEquals("Not all usages were found (calls in includedFile.bash and source.bash)", 2, usages.size());
    }
}
