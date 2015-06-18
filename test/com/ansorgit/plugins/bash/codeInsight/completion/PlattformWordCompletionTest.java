package com.ansorgit.plugins.bash.codeInsight.completion;

public class PlattformWordCompletionTest extends AbstractCompletionTest {
    public PlattformWordCompletionTest() {
        super("/codeInsight/completion/plattformWordCompletion");
    }

    public void _testPlattformCommentWordCompletion() throws Exception {
        configureByTestName();

        //completion in comments must not call the word completion
        checkNoItemsExpected();
    }
}
