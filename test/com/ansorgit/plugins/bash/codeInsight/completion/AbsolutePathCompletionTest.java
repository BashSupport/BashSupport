package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.file.BashFileType;

/**
 * User: jansorg
 * Date: 09.02.11
 * Time: 20:59
 */
public class AbsolutePathCompletionTest extends AbstractCompletionTest {
    @Override
    protected String getTestDir() {
        return "absolutePathCompletion";
    }

    public void testSimpleCompletion1() throws Throwable {
        String data = String.format("%sSimpleC<caret>", getTestDataPath());
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        complete();
        checkItems(getTestDataPath() + "SimpleCompletion.bash");
    }

    public void testSimpleCompletion2() throws Throwable {
        String data = String.format("%sSi<caret>", getTestDataPath());
        configureByText(BashFileType.BASH_FILE_TYPE, data);

        complete();
        checkItems(getTestDataPath() + "SimpleCompletion.bash");
    }
}
