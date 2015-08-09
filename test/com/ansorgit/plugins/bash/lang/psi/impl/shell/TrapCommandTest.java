package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.junit.Assert;

public class TrapCommandTest extends BashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/psi/word/";
    }

    public void testEvalWordContainer() throws Exception {
        PsiElement current = configure();

        PsiElement word = current;
        Assert.assertNotNull(word);
        Assert.assertTrue(word instanceof BashWordImpl);
        Assert.assertTrue("The element is not a valid injection host: " + word.getText(), ((BashWordImpl) word).isValidHost());
    }

    public void testEvalStringContainer() throws Exception {
        PsiElement current = configure();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a String: " + current, current instanceof BashStringImpl);
        Assert.assertTrue("The element is not a valid injection host: " + current.getText(), ((BashStringImpl) current).isValidHost());
    }

    protected PsiElement configure() {
        myFixture.setTestDataPath(getTestDataPath());
        myFixture.configureByFile(getTestName(true) + ".bash");

        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        if (element instanceof LeafPsiElement) {
            return element.getParent();
        }

        return element;
    }
}
