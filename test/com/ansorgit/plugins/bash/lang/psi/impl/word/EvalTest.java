package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
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
        Assert.assertTrue(word instanceof BashWordImpl);
        Assert.assertTrue("The element is not a valid injection host: " + word.getText(), ((BashWordImpl) word).isValidHost());
    }

    public void testEvalStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a String: " + current, current instanceof BashStringImpl);
        Assert.assertTrue("The element is not a valid injection host: " + current.getText(), ((BashStringImpl) current).isValidHost());
    }

}
