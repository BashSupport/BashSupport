package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.*;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.codeInsight.CodeInsightTestCase;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.junit.Assert;

public class BashPsiElementFactoryTest extends CodeInsightTestCase {

    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath();
    }

    public void testCreateDummyBashFile() throws Exception {
        PsiFile file = BashPsiElementFactory.createDummyBashFile(myProject, "echo");
        Assert.assertTrue(file instanceof BashFile);
    }

    public void testFileReference() throws Exception {
        PsiElement element = BashPsiElementFactory.createFileReference(myProject, "filename.bash");
        Assert.assertTrue("Not a file reference: " + element, element instanceof BashFileReference);
    }

    public void testCreateSymbol() throws Exception {
        PsiElement element = BashPsiElementFactory.createSymbol(myProject, "ABC");
        Assert.assertTrue(element instanceof BashFunctionDefName);
    }

    public void testCreateWord() throws Exception {
        PsiElement element = BashPsiElementFactory.createWord(myProject, "abc");
        Assert.assertTrue("element is not a word: " + element, element instanceof BashWord);
    }

    public void testCreateAssignmentWord() throws Exception {
        PsiElement element = BashPsiElementFactory.createAssignmentWord(myProject, "abc");
        Assert.assertTrue("element is not a assignment word: " + element, element.getNode().getElementType() == BashTokenTypes.ASSIGNMENT_WORD);
    }

    public void testCreateString() throws Exception {
        PsiElement element = BashPsiElementFactory.createString(myProject, "abc");
        Assert.assertTrue("element is not a string: " + element, element instanceof BashString);
    }

    public void testCreateVariable() throws Exception {
        PsiElement var = BashPsiElementFactory.createVariable(myProject, "abc", false);
        Assert.assertTrue("element is not a variable: " + var, var instanceof BashVar);

        PsiElement varBraces = BashPsiElementFactory.createVariable(myProject, "abc", true);
        Assert.assertTrue("element is not a variable: " + varBraces, varBraces instanceof BashVar);
    }

    public void testCreateShebang() throws Exception {
        PsiElement shebang = BashPsiElementFactory.createShebang(myProject, "abc", false);
        Assert.assertTrue(shebang instanceof BashShebang);

        PsiElement newlineShebang = BashPsiElementFactory.createShebang(myProject, "abc", true);
        Assert.assertTrue(newlineShebang instanceof BashShebang);
    }

    public void testCreateNewline() throws Exception {
        PsiElement element = BashPsiElementFactory.createNewline(myProject);
        Assert.assertTrue(element.getNode().getElementType() == BashTokenTypes.LINE_FEED);
    }

    public void testCreateComment() throws Exception {
        PsiComment element = BashPsiElementFactory.createComment(myProject, "abc");
        Assert.assertTrue("element is not a command: "+ element, element instanceof PsiComment);
    }
}