package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashComposedCommand;
import com.ansorgit.plugins.bash.lang.psi.eval.BashEvalBlock;
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
        Assert.assertTrue("Container is not an eval block", word instanceof BashEvalBlock);
    }

    public void testEvalStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashEvalBlock);
    }

    public void testEvalMultiStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashEvalBlock);

        Collection<BashEvalBlock> evalBlocks = PsiTreeUtil.findChildrenOfType(current.getParent(), BashEvalBlock.class);
        Assert.assertEquals("Expected three eval block", 3, evalBlocks.size());
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
        Assert.assertTrue("element is not an empty command (with empty evsl blok): " + current, current instanceof BashComposedCommand);
    }

    public void testEvalSmallStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashEvalBlock);
    }

    public void testEvalSmallWordContainers() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashEvalBlock);
        Assert.assertEquals("parent element must have two eval containers", 2, PsiTreeUtil.findChildrenOfType(current.getParent(), BashEvalBlock.class).size());
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
