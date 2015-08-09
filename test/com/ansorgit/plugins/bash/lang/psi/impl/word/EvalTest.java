package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.intellij.psi.PsiElement;
import org.junit.Assert;

public class EvalTest extends BashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/psi/word/";
    }

    public void testEvalContainer() throws Exception {
        PsiElement current = configure();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a String2: " + current, current.getNode().getElementType() == BashTokenTypes.STRING2);

        PsiElement word = current.getParent();
        Assert.assertNotNull(word);
        Assert.assertTrue(word instanceof BashWordImpl);
        Assert.assertTrue("The word element is not a valid injection host: " + word.getText(), ((BashWordImpl) word).isValidHost());
    }

    protected PsiElement configure() {
        myFixture.setTestDataPath(getTestDataPath());
        myFixture.configureByFile(getTestName(true) + ".bash");

        return myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    }
}
