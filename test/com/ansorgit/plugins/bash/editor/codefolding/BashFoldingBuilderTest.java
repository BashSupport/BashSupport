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

package com.ansorgit.plugins.bash.editor.codefolding;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jansorg
 */
public class BashFoldingBuilderTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testVariablesFolding() {
        try {
            BashProjectSettings.storedSettings(getProject()).setVariableFolding(true);
            String fileContent = "TMP_DIR=tmpDir\nDECOMPILED_SRC_DIR=\"$TMP_DIR/../../data/decompiled_src\"";
            PsiFile psiFile = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, fileContent);

            BashVariableFoldingBuilder builder = new BashVariableFoldingBuilder();
            FoldingDescriptor[] regions = builder.buildFoldRegions(psiFile.getNode(), myFixture.getDocument(psiFile));

            Assert.assertEquals(1, regions.length);
            Assert.assertEquals("(35,43)", regions[0].getRange().toString());
        } finally {
            BashProjectSettings.storedSettings(getProject()).setVariableFolding(true);
        }
    }

    @Test
    public void testHeredocFolding() throws Exception {
        FoldingDescriptor[] regions = buildRegions("cat - << EOF\nline 1\nline 2\nline 3\nEOF", BashHereDoc.class);

        Assert.assertEquals(1, regions.length);
        Assert.assertEquals("(13,33)", regions[0].getRange().toString());
    }

    @Test
    public void testHeredocFoldingWithVariables() throws Exception {
        FoldingDescriptor[] regions = buildRegions("cat - << EOF\nline $a\nline $b\nline $c\nEOF", BashHereDoc.class);

        Assert.assertEquals(1, regions.length);
        Assert.assertEquals("(13,36)", regions[0].getRange().toString());
    }

    @Test
    public void testHeredocFoldingWithSubExpressions() throws Exception {
        FoldingDescriptor[] regions = buildRegions("cat - << EOF\nline $(a)\nline ${a}\nline $((1+1))\nEOF", BashHereDoc.class);

        Assert.assertEquals(1, regions.length);
        Assert.assertEquals("(13,46)", regions[0].getRange().toString());
    }

    private <T extends PsiElement> FoldingDescriptor[] buildRegions(String fileContent, Class<T> foldedElementType) {
        PsiFile psiFile = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, fileContent);

        T hereDoc = PsiTreeUtil.findChildOfType(psiFile, foldedElementType);
        Assert.assertNotNull(hereDoc);

        BashFoldingBuilder builder = new BashFoldingBuilder();
        return builder.buildFoldRegions(hereDoc.getNode(), myFixture.getDocument(psiFile));
    }
}