package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.psi.eval.BashEvalBlock;
import com.intellij.psi.PsiElement;
import org.junit.Assert;

public class EvalTest extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/psi/word/";
    }

    public void testEvalWordContainer() throws Exception {
        PsiElement word = configurePsiAtCaret();
        Assert.assertNotNull(word);
        Assert.assertTrue("Container is not an eval block", word instanceof BashEvalBlock);
    }

    public void testEvalStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashEvalBlock);
    }
}
