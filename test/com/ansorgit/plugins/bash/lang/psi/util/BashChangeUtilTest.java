package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashFunctionDefName;
import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashComposedVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.codeInsight.CodeInsightTestCase;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.junit.Assert;

public class BashChangeUtilTest extends CodeInsightTestCase {

    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath();
    }

    public void testCreateDummyBashFile() throws Exception {
        PsiFile file = BashChangeUtil.createDummyBashFile(myProject, "echo");
        Assert.assertTrue(file instanceof BashFile);
    }

    public void testCreateSymbol() throws Exception {
        PsiElement element = BashChangeUtil.createSymbol(myProject, "ABC");
        Assert.assertTrue(element instanceof BashFunctionDefName);
    }

    public void testCreateWord() throws Exception {
        PsiElement element = BashChangeUtil.createWord(myProject, "abc");
        Assert.assertTrue("element is not a word: " + element, element instanceof BashWord);
    }

    public void testCreateAssignmentWord() throws Exception {
        PsiElement element = BashChangeUtil.createAssignmentWord(myProject, "abc");
        Assert.assertTrue("element is not a assignment word: " + element, element.getNode().getElementType() == BashTokenTypes.ASSIGNMENT_WORD);
    }

    public void testCreateString() throws Exception {
        PsiElement element = BashChangeUtil.createString(myProject, "abc");
        Assert.assertTrue("element is not a string: " + element, element instanceof BashString);
    }

    public void testCreateVariable() throws Exception {
        PsiElement var = BashChangeUtil.createVariable(myProject, "abc", false);
        Assert.assertTrue("element is not a variable: " + var, var instanceof BashVar);

        PsiElement varBraces = BashChangeUtil.createVariable(myProject, "abc", true);
        Assert.assertTrue("element is not a variable: " + varBraces, varBraces instanceof BashVar);
    }

    public void testCreateShebang() throws Exception {
        PsiElement shebang = BashChangeUtil.createShebang(myProject, "abc", false);
        Assert.assertTrue(shebang instanceof BashShebang);

        PsiElement newlineShebang = BashChangeUtil.createShebang(myProject, "abc", true);
        Assert.assertTrue(newlineShebang instanceof BashShebang);
    }

    public void testCreateNewline() throws Exception {
        PsiElement element = BashChangeUtil.createNewline(myProject);
        Assert.assertTrue(element.getNode().getElementType() == BashTokenTypes.LINE_FEED);
    }

    public void testCreateComment() throws Exception {
        PsiComment element = BashChangeUtil.createComment(myProject, "abc");
        Assert.assertTrue("element is not a command: "+ element, element instanceof PsiComment);
    }
}