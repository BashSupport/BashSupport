package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.file.BashFileType;
import junit.framework.AssertionFailedError;

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

    public void testSimpleCompletionShowHidden() throws Throwable {
        String prefix = getTestDataPath();
        String data = String.format("%s<caret>", prefix);
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        complete(2);

        try {
            checkItems(prefix + "SimpleCompletion.bash", prefix + ".hiddenDir/");
        } catch (AssertionFailedError e) {
            //fallback if the project is under version control
            checkItems(prefix + "SimpleCompletion.bash", prefix + ".hiddenDir/", prefix + ".svn/");
        }
    }
}
