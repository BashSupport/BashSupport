package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.file.BashFileType;
import org.junit.Assert;

import java.io.File;

/**
 * User: jansorg
 * Date: 09.02.11
 * Time: 20:59
 */
public class ShebangPathCompletionTest extends AbstractCompletionTest {
    public ShebangPathCompletionTest() {
        super("/codeInsight/completion/shebangPathCompletion");
    }

    public void testSimpleCompletionExecutable() throws Throwable {
        //make sure the file is executable
        String filePath = getFullTestDataPath() + "/SimpleCompletion.bash";
        Assert.assertTrue(new File(filePath).setExecutable(true));

        String data = String.format("#!%s/SimpleC<caret>", getFullTestDataPath());
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        complete();
        assertStringItems(filePath);
    }

    public void testSimpleCompletionNotExecutable() throws Throwable {
        //make sure the file is NOT executable
        String filePath = getFullTestDataPath() + "/SimpleCompletion.bash";
        Assert.assertTrue(new File(filePath).setExecutable(false));

        String data = String.format("#!%s/SimpleC<caret>", getTestDataPath());
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        complete();
        assertNull(myItems);
    }

    public void testNoCompletionPossible() throws Throwable {
        //make sure the file is NOT executable
        String filePath = getFullTestDataPath() + "/SimpleCompletion.bash";
        Assert.assertTrue(new File(filePath).setExecutable(false));

        String data = String.format("#!%s/NO<caret>", getTestDataPath());
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        complete();
        assertNull(myItems);
    }
}
