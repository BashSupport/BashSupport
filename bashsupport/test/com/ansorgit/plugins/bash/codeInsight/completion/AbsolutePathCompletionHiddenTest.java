package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.file.BashFileType;

/**
 * User: jansorg
 * Date: 09.02.11
 * Time: 20:59
 */
public class AbsolutePathCompletionHiddenTest extends AbstractCompletionTest {
    @Override
    protected String getTestDir() {
        return "absolutePathCompletionHidden";
    }

    public void testSimpleCompletionNoHidden() throws Throwable {
        String prefix = getTestDataPath();
        String data = String.format("%s<caret>", prefix);
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        complete(1);
        checkItems(prefix + "SimpleCompletion.bash");
    }

    public void testSimpleCompletionHiddenNoFirstCompletions() throws Throwable {
        String prefix = getTestDataPath();
        String data = String.format("%s.H<caret>", prefix);
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        //there should be completions if no files were found
        complete(1);
        checkItems(prefix + ".HiddenFile.bash");
    }

    public void testSimpleCompletionShowHidden() throws Throwable {
        String prefix = getTestDataPath();
        String data = String.format("%s<caret>", prefix);
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        complete(2);
        checkItems(prefix + "SimpleCompletion.bash", prefix + ".hiddenDir/");
    }
}
