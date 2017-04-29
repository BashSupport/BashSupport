/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.parser.eval;

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
    protected String getBasePath() {
        return "/parser/eval/";
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