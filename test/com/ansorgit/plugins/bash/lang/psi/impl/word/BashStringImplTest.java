package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

public class BashStringImplTest extends LightBashCodeInsightFixtureTestCase {
    public void testEmpty() throws Exception {
        BashString string = configureString("\"\"");

        Assert.assertTrue(string.isWrapped());
        Assert.assertTrue(string.isStatic());
        Assert.assertEquals(TextRange.create(1, 1), string.getTextContentRange());

        Assert.assertEquals("\"abcdefghijk\"", string.createEquallyWrappedString("abcdefghijk"));
    }

    public void testWrapped() throws Exception {
        BashString string = configureString("\"abc def\"");

        Assert.assertTrue(string.isWrapped());
        Assert.assertTrue(string.isStatic());
        Assert.assertEquals(TextRange.create(1, 8), string.getTextContentRange());

        Assert.assertEquals("\"abcdefghijk\"", string.createEquallyWrappedString("abcdefghijk"));
    }

    public void testWrappedDollar() throws Exception {
        BashString string = configureString("$\"abc def\"");

        Assert.assertTrue(string.isWrapped());
        Assert.assertTrue(string.isStatic());
        Assert.assertEquals(TextRange.create(2, 9), string.getTextContentRange());
        Assert.assertEquals("$\"abcdefghijk\"", string.createEquallyWrappedString("abcdefghijk"));
    }

    public void testDynamic() throws Exception {
        BashString string = configureString("$\"$VAR abc def\"");
        Assert.assertFalse(string.isStatic());
        Assert.assertEquals(TextRange.create(2, 14), string.getTextContentRange());
        Assert.assertEquals("$\"abcdefghijk\"", string.createEquallyWrappedString("abcdefghijk"));
    }

    public void testDynamic2() throws Exception {
        BashString string = configureString("$\"abcd $VAR\"");
        Assert.assertFalse(string.isStatic());
        Assert.assertEquals(TextRange.create(2, 11), string.getTextContentRange());
        Assert.assertEquals("$\"abcdefghijk\"", string.createEquallyWrappedString("abcdefghijk"));
    }

    @NotNull
    protected BashString configureString(String text) {
        PsiFile file = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, text);

        BashString string = PsiTreeUtil.findChildOfType(file, BashString.class);
        Assert.assertNotNull(string);
        return string;
    }
}