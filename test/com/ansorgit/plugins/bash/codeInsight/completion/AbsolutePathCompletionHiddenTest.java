package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.util.OSUtil;
import org.junit.Test;

public class AbsolutePathCompletionHiddenTest extends AbstractCompletionTest {
    public AbsolutePathCompletionHiddenTest() {
        super("/codeInsight/completion/absolutePathCompletionHidden");
    }

    @Test
    public void testSimpleCompletionNoHidden() throws Throwable {
        String prefix = getFullTestDataPath();

        configureByText(BashFileType.BASH_FILE_TYPE, String.format("%s<caret>", prefix));

        complete(1);

        assertStringItems(prefix + "/SimpleCompletion.bash", prefix + "/SimpleCompletion2.bash");
    }

    @Test
    public void testSimpleCompletionHiddenNoFirstCompletions() throws Throwable {
        String prefix = getFullTestDataPath();

        configureByText(BashFileType.BASH_FILE_TYPE, String.format("%s/.H<caret>", prefix));

        //there should be completions if no files were found
        complete(1);

        assertStringItems(prefix + "/.HiddenFile.bash", prefix + "/.HiddenFile2.bash");
    }

    @Test
    public void testSimpleCompletionShowHidden() throws Throwable {
        String prefix = getFullTestDataPath();
        String cygwinPrefix = OSUtil.toBashCompatible(prefix);

        configureByText(BashFileType.BASH_FILE_TYPE, String.format("%s<caret>", cygwinPrefix));

        complete(2);

        assertStringItems(cygwinPrefix + "/.hiddenDir/",
                cygwinPrefix + "/.HiddenFile.bash", cygwinPrefix + "/.HiddenFile2.bash",
                cygwinPrefix + "/SimpleCompletion.bash", cygwinPrefix + "/SimpleCompletion2.bash");
    }
}
