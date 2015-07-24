package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.*;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocStartMarker;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.codeInsight.CodeInsightTestCase;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.junit.Assert;
import org.junit.Test;

public class BashPsiElementFactoryTest extends CodeInsightTestCase {

    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath();
    }

    @Test
    public void testCreateDummyBashFile() throws Exception {
        PsiFile file = BashPsiElementFactory.createDummyBashFile(myProject, "echo");
        Assert.assertTrue(file instanceof BashFile);
    }

    @Test
    public void testFileReference() throws Exception {
        PsiElement element = BashPsiElementFactory.createFileReference(myProject, "filename.bash");
        Assert.assertTrue("Not a file reference: " + element, element instanceof BashFileReference);
    }

    @Test
    public void testCreateSymbol() throws Exception {
        PsiElement element = BashPsiElementFactory.createSymbol(myProject, "ABC");
        Assert.assertTrue(element instanceof BashFunctionDefName);
    }

    @Test
    public void testCreateWord() throws Exception {
        PsiElement element = BashPsiElementFactory.createWord(myProject, "abc");
        Assert.assertTrue("element is not a word: " + element, element instanceof BashWord);
    }

    @Test
    public void testCreateAssignmentWord() throws Exception {
        PsiElement element = BashPsiElementFactory.createAssignmentWord(myProject, "abc");
        Assert.assertTrue("element is not a assignment word: " + element, element.getNode().getElementType() == BashTokenTypes.ASSIGNMENT_WORD);
    }

    @Test
    public void testCreateString() throws Exception {
        PsiElement element = BashPsiElementFactory.createString(myProject, "abc");
        Assert.assertTrue("element is not a string: " + element, element instanceof BashString);
    }

    @Test
    public void testCreateStringWrapped() throws Exception {
        PsiElement element = BashPsiElementFactory.createString(myProject, "\"abc\"");
        Assert.assertTrue("element is not a string: " + element, element instanceof BashString);
    }

    @Test
    public void testCreateVariable() throws Exception {
        PsiElement var = BashPsiElementFactory.createVariable(myProject, "abc", false);
        Assert.assertTrue("element is not a variable: " + var, var instanceof BashVar);

        PsiElement varBraces = BashPsiElementFactory.createVariable(myProject, "abc", true);
        Assert.assertTrue("element is not a variable: " + varBraces, varBraces instanceof BashVar);
    }

    @Test
    public void testCreateShebang() throws Exception {
        PsiElement shebang = BashPsiElementFactory.createShebang(myProject, "abc", false);
        Assert.assertTrue(shebang instanceof BashShebang);

        PsiElement newlineShebang = BashPsiElementFactory.createShebang(myProject, "abc", true);
        Assert.assertTrue(newlineShebang instanceof BashShebang);
    }

    @Test
    public void testCreateNewline() throws Exception {
        PsiElement element = BashPsiElementFactory.createNewline(myProject);
        Assert.assertTrue(element.getNode().getElementType() == BashTokenTypes.LINE_FEED);
    }

    @Test
    public void testCreateComment() throws Exception {
        PsiComment element = BashPsiElementFactory.createComment(myProject, "abc");
        Assert.assertTrue("element is not a command: " + element, element instanceof PsiComment);
    }

    @Test
    public void testCommand() throws Exception {
        PsiElement element = BashPsiElementFactory.createCommand(myProject, "externalCOmmand");
        Assert.assertTrue("element is not a command: " + element, element instanceof BashGenericCommand);
    }

    @Test
    public void testHeredocStartMarker() throws Exception {
        PsiElement element = BashPsiElementFactory.createHeredocStartMarker(myProject, "EOF");
        Assert.assertTrue("element is not a start marker: " + element, element instanceof BashHereDocStartMarker);
    }

    @Test
    public void testHeredocEndMarker() throws Exception {
        PsiElement element = BashPsiElementFactory.createHeredocEndMarker(myProject, "EOF");
        Assert.assertTrue("element is not a start marker: " + element, element instanceof BashHereDocEndMarker);
    }
}