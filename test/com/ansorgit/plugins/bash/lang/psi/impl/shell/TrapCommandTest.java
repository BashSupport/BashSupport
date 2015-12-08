package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.eval.BashEvalBlock;
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashStringImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashWordImpl;
import com.intellij.psi.PsiElement;
import org.junit.Assert;

public class TrapCommandTest extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/psi/shell/trapCommand/";
    }

    public void testInjectionHostWordSingleWord() throws Exception {
        PsiElement word = configurePsiAtCaret();
        Assert.assertNotNull(word);
        Assert.assertTrue("Element is not a eval block: " + word, word instanceof BashEvalBlock);

        // a single word in a string used as trap command must not be marked as an injection host
        // because it ist just a single command. Resolving this as a function is faster as injection handling
        //Assert.assertFalse("The element must not be an injection host: " + word.getText(), ((BashWordImpl) word).isValidBashLanguageHost());
    }

    public void testInjectionHostStringSingleWord() throws Exception {
        PsiElement word = configurePsiAtCaret();
        Assert.assertNotNull(word);
        Assert.assertTrue(word instanceof BashEvalBlock);

        // a single word in a string used as trap command must not be marked as an injection host
        // because it ist just a single command. Resolving this as a function is faster as injection handling
        //Assert.assertFalse("The element must not be an injection host: " + word.getText(), ((BashStringImpl) word).isValidBashLanguageHost());
    }

    public void testInjectionHostWord() throws Exception {
        PsiElement word = configurePsiAtCaret();
        Assert.assertNotNull(word);
        Assert.assertTrue(word instanceof BashEvalBlock);
    }

    public void testInjectionHostString() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a String: " + current, current instanceof BashEvalBlock);
    }

    public void testInjectionWordUnquotedWord() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a String: " + current, current instanceof BashGenericCommand);
    }

}
