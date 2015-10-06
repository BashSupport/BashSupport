package com.ansorgit.plugins.bash.editor;

import com.ansorgit.plugins.bash.lang.psi.impl.word.BashWordImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.intellij.codeInsight.CodeInsightTestCase;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
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
    public void testEscaped1() throws Exception {
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
    public void testEscaped2() throws Exception {
        // unescpaed content: a\\"'a
        // java escapes \\ to \\\\
        // bash escapes \\\\ to \\\\\\\\
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
    public void testEscaped3() throws Exception {
        StringBuilder content = unescapeContent("$'{ \"key1\": \"content\",\n\"key2\": \"content \\\\\" \\\\\" \\'\"\n}'");

        //decoded text is a\\"'a
        Assert.assertEquals("{ \"key1\": \"content\",\n\"key2\": \"content \\\" \\\" '\"\n}", content.toString());
    }

    /**
     * Tests that unknown escape sequences are not unescaped (Bash doesn't do this, too)
     * @throws Exception
     */
    @Test
    public void testEscaped4() throws Exception {
        StringBuilder content = unescapeContent("$'\\p\\e'");

        //decoded text is a\\"'a
        Assert.assertEquals("\\p\\e", content.toString());
    }

    @NotNull
    private StringBuilder unescapeContent(String source) {
        BashWordImpl psiElement = (BashWordImpl) BashPsiElementFactory.createWord(myProject, source);

        LiteralTextEscaper<? extends PsiLanguageInjectionHost> textEscaper = psiElement.createLiteralTextEscaper();

        StringBuilder content = new StringBuilder();
        TextRange range = psiElement.getTextContentRange();
        textEscaper.decode(range, content);
        return content;
    }
}