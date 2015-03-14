package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.codeInsight.completion.CompletionType;

public class AbsolutePathCompletionHiddenTest extends AbstractCompletionTest {
    public AbsolutePathCompletionHiddenTest() {
        super("/codeInsight/completion/absolutePathCompletionHidden");
    }

    public void testSimpleCompletionNoHidden() throws Throwable {
        String prefix = getFullTestDataPath();

        configureByText(BashFileType.BASH_FILE_TYPE, String.format("%s<caret>", prefix));

        complete(1);

        assertStringItems(prefix + "/SimpleCompletion.bash", prefix + "/SimpleCompletion2.bash");
    }

    public void testSimpleCompletionHiddenNoFirstCompletions() throws Throwable {
        String prefix = getFullTestDataPath();

        configureByText(BashFileType.BASH_FILE_TYPE, String.format("%s/.H<caret>", prefix));

        //there should be completions if no files were found
        complete(1);

        assertStringItems(prefix + "/.HiddenFile.bash", prefix + "/.HiddenFile2.bash");
    }

    public void testSimpleCompletionShowHidden() throws Throwable {
        String prefix = getFullTestDataPath();

        configureByText(BashFileType.BASH_FILE_TYPE, String.format("%s<caret>", prefix));

        complete(2);

        assertStringItems(prefix + "/.hiddenDir/",
                prefix + "/.HiddenFile.bash", prefix + "/.HiddenFile2.bash",
                prefix + "/SimpleCompletion.bash", prefix + "/SimpleCompletion2.bash");
    }
}
