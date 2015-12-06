package com.ansorgit.plugins.bash.lang.parser.eval;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.eval.BashEvalBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Assert;
import org.junit.Test;

public class UnescapingPsiBuilderTest extends LightBashCodeInsightFixtureTestCase {

    @Test
    public void testNoEscaping() throws Exception {
        PsiFile file = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, "eval 'hello there, this is me!'");

        BashEvalBlock evalBlock = PsiTreeUtil.findChildOfType(file, BashEvalBlock.class);
        Assert.assertNotNull(evalBlock);

        PsiElement[] children = evalBlock.getChildren();
        Assert.assertEquals(5, children.length);
    }
}