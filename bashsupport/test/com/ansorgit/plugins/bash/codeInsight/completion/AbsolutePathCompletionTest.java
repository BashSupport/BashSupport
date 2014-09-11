package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.file.BashFileType;

public class AbsolutePathCompletionTest extends AbstractCompletionTest {
    private static final String PREFIX = "codeInsight/completion/absolutePathCompletion";

    public AbsolutePathCompletionTest() {
        super("/" + PREFIX);
    }

    public void testSimpleCompletion1() throws Throwable {
        configureByText(BashFileType.BASH_FILE_TYPE, String.format("S<caret>", PREFIX));

        complete(1);
        assertStringItems(PREFIX + "/SimpleCompletion.bash", PREFIX + "/SimpleCompletion2.bash");
    }

    public void testSimpleCompletion2() throws Throwable {
        configureByText(BashFileType.BASH_FILE_TYPE, String.format("Si<caret>", PREFIX));

        complete(1);
        assertStringItems(PREFIX + "/SimpleCompletion.bash", PREFIX + "/SimpleCompletion2.bash");
    }
}
