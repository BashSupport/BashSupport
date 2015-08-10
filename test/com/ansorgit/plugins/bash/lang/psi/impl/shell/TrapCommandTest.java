package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashStringImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashWordImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.junit.Assert;

public class TrapCommandTest extends BashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/psi/shell/trapCommand/";
    }

    public void testInjectionHostWordSingleWord() throws Exception {
        PsiElement word = configure();
        Assert.assertNotNull(word);
        Assert.assertTrue(word instanceof BashWordImpl);

        // a single word in a string used as trap command must not be marked as an injection host
        // because it ist just a single command. Resolving this as a function is faster as injection handling
        Assert.assertFalse("The element must not be an injection host: " + word.getText(), ((BashWordImpl) word).isValidHost());
    }

    public void testInjectionHostStringSingleWord() throws Exception {
        PsiElement word = configure();
        Assert.assertNotNull(word);
        Assert.assertTrue(word instanceof BashStringImpl);

        // a single word in a string used as trap command must not be marked as an injection host
        // because it ist just a single command. Resolving this as a function is faster as injection handling
        Assert.assertFalse("The element must not be an injection host: " + word.getText(), ((BashStringImpl) word).isValidHost());
    }

    public void testInjectionHostWord() throws Exception {
        PsiElement word = configure();
        Assert.assertNotNull(word);
        Assert.assertTrue(word instanceof BashWordImpl);
        Assert.assertTrue("The element is not a valid injection host: " + word.getText(), ((BashWordImpl) word).isValidHost());
    }

    public void testInjectionHostString() throws Exception {
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
