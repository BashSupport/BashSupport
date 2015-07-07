package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.file.BashFileType;
import org.junit.Test;

public class AbsolutePathCompletionTest extends AbstractCompletionTest {
    private static final String PREFIX = "codeInsight/completion/absolutePathCompletion";

    public AbsolutePathCompletionTest() {
        super("/" + PREFIX);
    }

    @Test
    public void testSimpleCompletion1() throws Throwable {
        configureByText(BashFileType.BASH_FILE_TYPE, String.format("%s/S<caret>", getFullTestDataPath()));

        complete(1);
        assertStringItems(getFullTestDataPath() + "/SimpleCompletion.bash", getFullTestDataPath() + "/SimpleCompletion2.bash");
    }

    @Test
    public void testSimpleCompletion2() throws Throwable {
        configureByText(BashFileType.BASH_FILE_TYPE, String.format("%s/Si<caret>", getFullTestDataPath()));

        complete(1);
        assertStringItems(getFullTestDataPath() + "/SimpleCompletion.bash", getFullTestDataPath() + "/SimpleCompletion2.bash");
    }
}
