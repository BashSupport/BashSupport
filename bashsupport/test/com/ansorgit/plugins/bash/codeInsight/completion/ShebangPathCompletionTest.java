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
    @Override
    protected String getTestDir() {
        return "shebangPathCompletion";
    }

    public void testSimpleCompletionExecutable() throws Throwable {
        //make sure the file is executable
        String filePath = getTestDataPath() + "SimpleCompletion.bash";
        Assert.assertTrue(new File(filePath).setExecutable(true));

        String data = String.format("#!%sSimpleC<caret>", getTestDataPath());
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        complete();
        checkItems(filePath);
    }

    public void testSimpleCompletionNotExecutable() throws Throwable {
        //make sure the file is NOT executable
        String filePath = getTestDataPath() + "SimpleCompletion.bash";
        Assert.assertTrue(new File(filePath).setExecutable(false));

        String data = String.format("#!%sSimpleC<caret>", getTestDataPath());
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        complete();
        checkItems(NO_COMPLETIONS);
    }

    public void testNoCompletionPossible() throws Throwable {
        //make sure the file is NOT executable
        String filePath = getTestDataPath() + "SimpleCompletion.bash";
        Assert.assertTrue(new File(filePath).setExecutable(false));

        String data = String.format("#!%sNO<caret>", getTestDataPath());
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        complete();
        checkItems(NO_COMPLETIONS);
    }
}
