package com.ansorgit.plugins.bash.lang.parser.eval;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.eval.BashEvalBlock;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

public class UnescapingPsiBuilderTest extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/parser/eval/";
    }

    @Test
    public void testNoEscaping() throws Exception {
        PsiFile file = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, "eval 'hello there, this is me!'");

        BashEvalBlock evalBlock = PsiTreeUtil.findChildOfType(file, BashEvalBlock.class);
        Assert.assertNotNull(evalBlock);

        PsiElement[] children = evalBlock.getChildren();
        Assert.assertEquals(1, children.length);
    }

    @Test
    public void testBasic() throws Exception {
        BashProjectSettings.storedSettings(getProject()).setEvalEscapesEnabled(true);

        //this feature needs the experimental settings
        try {
            PsiFile file = myFixture.configureByFile("basic/basic.bash");
            Assert.assertNotNull(file);

            Collection<BashEvalBlock> evalBlocks = PsiTreeUtil.findChildrenOfType(file, BashEvalBlock.class);
            Assert.assertNotNull(evalBlocks);
            Assert.assertEquals(2, evalBlocks.size());

            Iterator<BashEvalBlock> iterator = evalBlocks.iterator();

            BashEvalBlock first = iterator.next();
            Assert.assertEquals(1, first.getChildren().length);

            BashEvalBlock second = iterator.next();
            Assert.assertEquals(2, second.getChildren().length);
        } finally {
            BashProjectSettings.storedSettings(getProject()).setEvalEscapesEnabled(false);
        }
    }

    @Test
    public void testSimpleFile() throws Exception {
        PsiFile file = myFixture.configureByFile("simpleFile/source.bash");
        Assert.assertNotNull(file);

        Collection<BashEvalBlock> evalBlocks = PsiTreeUtil.findChildrenOfType(file, BashEvalBlock.class);
        Assert.assertNotNull(evalBlocks);
        Assert.assertEquals(1, evalBlocks.size());

        Iterator<BashEvalBlock> iterator = evalBlocks.iterator();
        BashEvalBlock first = iterator.next();
        Assert.assertEquals(1, first.getChildren().length);
    }
}