package com.ansorgit.plugins.bash.codeInsight.completion;

public class KeywordCompletionTest extends AbstractCompletionTest {
    public KeywordCompletionTest() {
        super("/codeInsight/completion/keywordCompletion");
    }

    public void testDefaultKeywordCompletionIf() throws Exception {
        configureByTestName();

        checkItems("if", "elif", "shift");
    }

    public void testDefaultKeywordCompletionWhile() throws Exception {
        configureByTestName();

        checkItems("while");
    }

    public void testCommentKeywordCompletion() throws Exception {
        configureByTestName();

        //completion in comments must not call the word completion
        checkNoItemsExpected();
    }
}
