package com.ansorgit.plugins.bash.editor;

import com.ansorgit.plugins.bash.lang.psi.impl.word.BashWordImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.intellij.codeInsight.CodeInsightTestCase;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.junit.Assert;
import org.junit.Test;

public class BashEnhancedLiteralTextEscaperTest extends CodeInsightTestCase {
    @Test
    public void testUnescpaed() throws Exception {
        BashWordImpl psiElement = (BashWordImpl) BashPsiElementFactory.createWord(myProject, "$'a'");
        Assert.assertNotNull(psiElement);

        LiteralTextEscaper<? extends PsiLanguageInjectionHost> textEscaper = psiElement.createLiteralTextEscaper();

        StringBuilder content = new StringBuilder();
        TextRange range = psiElement.getTextContentRange();
        textEscaper.decode(range, content);
        Assert.assertEquals("a", content.toString());

        //check the offsets
        Assert.assertEquals(2, textEscaper.getOffsetInHost(0, range));
    }

    @Test
    public void testEscpaed1() throws Exception {
        BashWordImpl psiElement = (BashWordImpl) BashPsiElementFactory.createWord(myProject, "$'a\\'a'");
        Assert.assertNotNull(psiElement);

        LiteralTextEscaper<? extends PsiLanguageInjectionHost> textEscaper = psiElement.createLiteralTextEscaper();

        StringBuilder content = new StringBuilder();
        TextRange range = psiElement.getTextContentRange();
        textEscaper.decode(range, content);
        Assert.assertEquals("a'a", content.toString());

        //check the offsets
        Assert.assertEquals(2, textEscaper.getOffsetInHost(0, range)); // a at 2
        Assert.assertEquals(3, textEscaper.getOffsetInHost(1, range)); // ' at 3-4
        Assert.assertEquals(5, textEscaper.getOffsetInHost(2, range)); // a at 5
    }

    @Test
    public void testEscpaed2() throws Exception {
        // unescpaed content: a\\"'a
        // java escaping doubles the backslash
        // bash escaped \\ to \\\\
        BashWordImpl psiElement = (BashWordImpl) BashPsiElementFactory.createWord(myProject, "$'a\\\\\\\\\"\\'a\\'");
        Assert.assertNotNull(psiElement);

        LiteralTextEscaper<? extends PsiLanguageInjectionHost> textEscaper = psiElement.createLiteralTextEscaper();

        StringBuilder content = new StringBuilder();
        TextRange range = psiElement.getTextContentRange();
        textEscaper.decode(range, content);

        //decoded text is a\\"'a
        Assert.assertEquals("a\\\\\"'a", content.toString());

        //check the offsets
        Assert.assertEquals(2, textEscaper.getOffsetInHost(0, range)); // a at 2
        Assert.assertEquals(3, textEscaper.getOffsetInHost(1, range)); // \ at 3-4
        Assert.assertEquals(5, textEscaper.getOffsetInHost(2, range)); // \ at 5-6
        Assert.assertEquals(7, textEscaper.getOffsetInHost(3, range)); // " at 7
        Assert.assertEquals(8, textEscaper.getOffsetInHost(4, range)); // ' at 8-9
        Assert.assertEquals(10, textEscaper.getOffsetInHost(5, range)); // a at 10
    }

    @Test
    public void testEscpaed3() throws Exception {
        // unescaped content: { "key1": "content",\n"key2": "content \\" \'"\n}
        BashWordImpl psiElement = (BashWordImpl) BashPsiElementFactory.createWord(myProject, "$'{ \"key1\": \"content\",\n\"key2\": \"content \\\\\\\\\" \\'\"\n}'");
        Assert.assertNotNull(psiElement);

        LiteralTextEscaper<? extends PsiLanguageInjectionHost> textEscaper = psiElement.createLiteralTextEscaper();

        StringBuilder content = new StringBuilder();
        TextRange range = psiElement.getTextContentRange();
        textEscaper.decode(range, content);

        //decoded text is a\\"'a
        Assert.assertEquals("{ \"key1\": \"content\",\n\"key2\": \"content \\\\\" '\"\n}", content.toString());
    }
}