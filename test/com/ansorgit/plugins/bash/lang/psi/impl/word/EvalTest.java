package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashComposedCommand;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Assert;

import java.util.Collection;
import java.util.List;

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

    public void testEvalMultiStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashString);

        Collection<BashString> stringContainer = PsiTreeUtil.findChildrenOfType(current.getParent(), BashString.class);
        Assert.assertEquals("Expected three eval block", 3, stringContainer.size());
        for (BashString string : stringContainer) {
            Assert.assertTrue("Should be a valid injection host", ((BashStringImpl)string).isValidHost());
        }
    }

    public void testEvalEmptyStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not an empty string: " + current, current instanceof BashString);
        Assert.assertTrue("element is not an empty string: " + current, current.getText().equals("\"\""));
    }

    public void testEvalEmptyWordContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("empty eval string is not a word " + current, current instanceof BashWord);
        Assert.assertFalse("empty eval block must not be a valid injection host for bash " + current, ((BashLanguageInjectionHost)current).isValidBashLanguageHost());
    }

    public void testEvalSmallStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashString);
        Assert.assertTrue("The element is not a valid injection host: " + current.getText(), ((BashStringImpl) current).isValidHost());
    }

    public void testEvalSmallWordContainers() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashWord);
        Assert.assertTrue("The element is not a valid injection host: " + current.getText(), ((BashWordImpl) current).isValidHost());
    }

    public void testEvalError() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);

        final List<PsiErrorElement> errors = Lists.newLinkedList();
        BashPsiUtils.visitRecursively(myFixture.getFile(), new BashVisitor() {
            @Override
            public void visitErrorElement(PsiErrorElement element) {
                errors.add(element);
            }
        });

        //Assert.assertEquals("1 error needs to be found", 1, errors.size());
    }
}
