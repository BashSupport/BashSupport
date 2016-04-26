package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashCharSequence;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

public class BashWordImplTest extends LightBashCodeInsightFixtureTestCase {
    public void testEmpty() throws Exception {
        BashCharSequence string = configureWord("''");

        Assert.assertTrue(string.isWrapped());
        Assert.assertTrue(string.isStatic());
        Assert.assertEquals(TextRange.create(1, 1), string.getTextContentRange());

        Assert.assertEquals("'abcdefghijk'", string.createEquallyWrappedString("abcdefghijk"));
    }

    public void testWrapped() throws Exception {
        BashCharSequence string = configureWord("'abc def'");

        Assert.assertTrue(string.isWrapped());
        Assert.assertTrue(string.isStatic());
        Assert.assertEquals(TextRange.create(1, 8), string.getTextContentRange());

        Assert.assertEquals("'abcdefghijk'", string.createEquallyWrappedString("abcdefghijk"));
    }

    public void testWrappedDollar() throws Exception {
        BashCharSequence string = configureWord("$'abc def'");

        Assert.assertTrue(string.isWrapped());
        Assert.assertTrue(string.isStatic());
        Assert.assertEquals(TextRange.create(2, 9), string.getTextContentRange());
        Assert.assertEquals("$'abcdefghijk'", string.createEquallyWrappedString("abcdefghijk"));
    }

    public void testDynamic() throws Exception {
        BashCharSequence string = configureWord("$'$VAR abc def'");
        Assert.assertTrue(string.isStatic());
        Assert.assertEquals(TextRange.create(2, 14), string.getTextContentRange());
        Assert.assertEquals("$'abcdefghijk'", string.createEquallyWrappedString("abcdefghijk"));
    }

    public void testDynamic2() throws Exception {
        BashCharSequence string = configureWord("$'abcd $VAR'");
        Assert.assertTrue(string.isStatic());
        Assert.assertEquals(TextRange.create(2, 11), string.getTextContentRange());
        Assert.assertEquals("$'abcdefghijk'", string.createEquallyWrappedString("abcdefghijk"));
    }

    public void testUnwrapped() throws Exception {
        BashWord string = configureWord("abcde");
        Assert.assertTrue(string.isStatic());
        Assert.assertFalse(string.isWrapped());
        Assert.assertFalse("A simple sring should not be wrappable (no unnecessary warning about wrapping a single word)", string.isWrappable());
        Assert.assertEquals(TextRange.create(0, 5), string.getTextContentRange());
        Assert.assertEquals("abcdefghijk", string.createEquallyWrappedString("abcdefghijk"));
    }

    public void testUnwrappedDynamic() throws Exception {
        BashWord string = configureWord("abcde${ABC}abc");
        Assert.assertFalse(string.isStatic());
        Assert.assertFalse(string.isWrapped());
        Assert.assertFalse(string.isWrappable());
        Assert.assertEquals(TextRange.create(0, 14), string.getTextContentRange());
        Assert.assertEquals("abcdefghijk", string.createEquallyWrappedString("abcdefghijk"));
    }

    @NotNull
    private BashWord configureWord(String text) {
        PsiFile file = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, text);

        BashWord string = PsiTreeUtil.findChildOfType(file, BashWord.class);
        Assert.assertNotNull(string);
        return string;
    }
}