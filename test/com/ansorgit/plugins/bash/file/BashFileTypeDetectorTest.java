package com.ansorgit.plugins.bash.file;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.intellij.codeInsight.CodeInsightTestCase;
import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jansorg
 */
@Ignore("The FileDetector doesn't seem to work in TestCases in 13.x")
public class BashFileTypeDetectorTest extends CodeInsightTestCase {
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/file/";
    }

    protected void assertIsBash(String filename) throws Exception {
        configureByFile(filename, null);

        VirtualFile virtualFile = getVirtualFile(filename);
        Assert.assertEquals(BashFileType.BASH_FILE_TYPE, virtualFile.getFileType());
    }

    protected void assertIsNotBash(String filename) throws Exception {
        configureByFile(filename, null);

        VirtualFile virtualFile = getVirtualFile(filename);
        Assert.assertNotEquals(BashFileType.BASH_FILE_TYPE, virtualFile.getFileType());
    }

    @Test
    public void testBashExtension() throws Exception {
        assertIsBash("file.bash");
    }

    @Test
    public void testShExtension() throws Exception {
        assertIsBash("file.sh");
    }

    @Test
    public void testBashShebang() throws Exception {
        assertIsBash("Bash");
    }

    @Test
    public void testUsrBinBashShebang() throws Exception {
        assertIsBash("UsrBinBash");
    }

    @Test
    public void testShShebang() throws Exception {
        assertIsBash("Sh");
    }

    @Test
    public void testUsrBinShShebang() throws Exception {
        assertIsBash("UsrBinSh");
    }

    @Test
    public void testEnvBashShebang() throws Exception {
        assertIsBash("EnvBash");
    }

    @Test
    public void testEnvShShebang() throws Exception {
        assertIsBash("EnvSh");
    }

    @Test
    public void testNegativePerlShebang() throws Exception {
        assertIsNotBash("Perl");
    }

    @Test
    public void testNegativeCShellShebang() throws Exception {
        assertIsNotBash("csh");
    }
}
