package com.ansorgit.plugins.bash.codeInsight.completion;

import org.junit.Test;

public class KeywordCompletionTest extends AbstractCompletionTest {
    public KeywordCompletionTest() {
        super("/codeInsight/completion/keywordCompletion");
    }

    @Test
    public void testDefaultKeywordCompletionIf() throws Exception {
        configureByTestName();

        checkItems("if", "elif", "shift");
    }

    @Test
    public void testDefaultKeywordCompletionWhile() throws Exception {
        configureByTestName();

        checkItems("while");
    }

    @Test
    public void testCommentKeywordCompletion() throws Exception {
        configureByTestName();

        //completion in comments must not call the word completion
        checkNoItemsExpected();
    }
}
