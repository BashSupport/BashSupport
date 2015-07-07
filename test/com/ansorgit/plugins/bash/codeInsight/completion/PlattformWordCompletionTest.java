package com.ansorgit.plugins.bash.codeInsight.completion;

import org.junit.Test;

public class PlattformWordCompletionTest extends AbstractCompletionTest {
    public PlattformWordCompletionTest() {
        super("/codeInsight/completion/plattformWordCompletion");
    }

    @Test
    public void testPlattformCommentWordCompletion() throws Exception {
        configureByTestName();

        //completion in comments must not call the word completion
        checkNoItemsExpected();
    }
}
