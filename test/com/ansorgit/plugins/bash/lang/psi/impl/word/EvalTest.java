package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarUse;
import com.ansorgit.plugins.bash.lang.psi.eval.BashEvalBlock;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

public class EvalTest extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/psi/word/";
    }

    @Test
    public void testEvalWordContainer() throws Exception {
        PsiElement word = configurePsiAtCaret();
        Assert.assertNotNull(word);
        Assert.assertTrue("Container is not an eval block", word instanceof BashEvalBlock);
    }

    @Test
    public void testEvalStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashEvalBlock);
    }

    @Test
    public void testEvalMultiStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashEvalBlock);

        Collection<BashEvalBlock> evalBlocks = PsiTreeUtil.findChildrenOfType(current.getParent(), BashEvalBlock.class);
        Assert.assertEquals("Expected three eval block", 3, evalBlocks.size());
    }

    @Test
    public void testEvalEmptyStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
//        Assert.assertTrue("element is not an empty string: " + current, current instanceof BashString);
        Assert.assertTrue("element is not an empty string: " + current, current.getText().equals("\"\""));
    }

    @Test
    public void testEvalEmptyWordContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not an empty string: " + current, current.getText().equals("''"));
//        Assert.assertTrue("element is not an empty command (with empty evsl blok): " + current, current instanceof BashComposedCommand);
    }

    @Test
    public void testEvalSmallStringContainer() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashEvalBlock);
    }

    @Test
    public void testEvalSmallWordContainers() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashEvalBlock);
        Assert.assertEquals("parent element must have two eval containers", 2, PsiTreeUtil.findChildrenOfType(current.getParent(), BashEvalBlock.class).size());
    }

    @Test
    public void testEvalEcho() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a var: " + current, current instanceof BashVar);
        Assert.assertNotNull("element is not in an eval block: " + current, PsiTreeUtil.getParentOfType(current, BashEvalBlock.class));

        Assert.assertNull("File must not contain errors", PsiTreeUtil.findChildOfType(current.getContainingFile(), PsiErrorElement.class));
    }

    @Test
    public void testEvalEcho2() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a eval block: " + current, current instanceof BashVar);
        Assert.assertNotNull("element is not in an eval block: " + current, PsiTreeUtil.getParentOfType(current, BashEvalBlock.class));

        Assert.assertNull("File must not contain errors", PsiTreeUtil.findChildOfType(current.getContainingFile(), PsiErrorElement.class));
    }

    @Test
    public void testIssue286() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a variable: " + current, current instanceof BashVarUse);

        PsiElement def = current.getReference().resolve();
        Assert.assertNotNull("var must resolve: " + def, def);
        Assert.assertTrue("var must resolve to a var def: " + def, def instanceof BashVarDef);

        //make sure that the eval block covers all expected data
        BashEvalBlock evalBlock = PsiTreeUtil.getParentOfType(def, BashEvalBlock.class);
        Assert.assertNotNull("The eval block must have been parsed", evalBlock);
        Assert.assertEquals("Eval must cover all text", "value=\\$myvar${index}", evalBlock.getText());

        Assert.assertEquals("The list of errors must be empty", 0, collectPsiErrors().size());
    }

    @Test
    public void testIssue330() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a variable: " + current, current instanceof BashVarUse);

        PsiElement def = current.getReference().resolve();
        Assert.assertNotNull("var must resolve: " + def, def);
        Assert.assertTrue("var must resolve to a var def: " + def, def instanceof BashVarDef);

        //make sure that the eval block covers all expected data
        BashEvalBlock evalBlock = PsiTreeUtil.getParentOfType(current, BashEvalBlock.class);
        Assert.assertNotNull("The eval block must have been parsed", evalBlock);
        Assert.assertEquals("Eval must cover all text", "\"$a=(x)\"", evalBlock.getText());

        Assert.assertEquals("The list of errors must be empty", 0, collectPsiErrors().size());
    }

    @Test
    public void testIssue330Var() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);
        Assert.assertTrue("element is not a variable: " + current, current instanceof BashVarUse);

        PsiElement def = current.getReference().resolve();
        Assert.assertNotNull("var must resolve: " + def, def);
        Assert.assertTrue("var must resolve to a var def: " + def, def instanceof BashVarDef);

        Assert.assertEquals("The list of errors must be empty", 0, collectPsiErrors().size());
    }

    @Test
    @Ignore
    public void _testEvalError() throws Exception {
        PsiElement current = configurePsiAtCaret();

        Assert.assertNotNull(current);

        List<PsiErrorElement> errors = collectPsiErrors();

        Assert.assertEquals("1 error needs to be found", 1, errors.size());
    }

    private List<PsiErrorElement> collectPsiErrors() {
        final List<PsiErrorElement> errors = Lists.newLinkedList();
        BashPsiUtils.visitRecursively(myFixture.getFile(), new BashVisitor() {
            @Override
            public void visitErrorElement(PsiErrorElement element) {
                errors.add(element);
            }
        });

        return errors;
    }
}
