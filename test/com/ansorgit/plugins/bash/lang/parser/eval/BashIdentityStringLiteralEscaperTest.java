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
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashWordImpl;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class BashIdentityStringLiteralEscaperTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testEncoding() throws Exception {
        PsiFile psiFile = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, " 'abc\n'");

        BashWordImpl word = PsiTreeUtil.findChildOfType(psiFile, BashWordImpl.class);
        Assert.assertNotNull(word);

        LiteralTextEscaper<? extends PsiLanguageInjectionHost> escaper = word.createLiteralTextEscaper();
        Assert.assertTrue(escaper instanceof BashIdentityStringLiteralEscaper<?>);

        TextRange range = TextRange.allOf(word.getUnwrappedCharSequence());
        Assert.assertEquals(0, escaper.getOffsetInHost(0, range));
        Assert.assertEquals(1, escaper.getOffsetInHost(1, range));
        Assert.assertEquals(2, escaper.getOffsetInHost(2, range));
        Assert.assertEquals(3, escaper.getOffsetInHost(3, range));
        Assert.assertEquals(4, escaper.getOffsetInHost(4, range));
    }
}