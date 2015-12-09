package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashTrapCommand;
import com.ansorgit.plugins.bash.lang.psi.eval.BashEvalBlock;
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
    }

    public void testInjectionHostStringSingleWord() throws Exception {
        PsiElement word = configurePsiAtCaret();
        Assert.assertNotNull(word);
        Assert.assertTrue(word instanceof BashEvalBlock);
    }

    public void testInjectionHostWord() throws Exception {
        PsiElement word = configurePsiAtCaret();
        Assert.assertNotNull(word);
        Assert.assertTrue(word instanceof BashEvalBlock);
    }

    public void testInjectionHostString() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not an eval block: " + current, current instanceof BashEvalBlock);
    }

    public void testInjectionWordUnquotedWord() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a command: " + current, current instanceof BashGenericCommand);
    }

    public void testInjectionEmptyWord() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not an empty trap command: " + current, current instanceof BashTrapCommand);
    }

    public void testInjectionEmptyString() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not an empty trap command: " + current, current instanceof BashString);
    }
}
