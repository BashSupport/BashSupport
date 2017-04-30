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

package com.ansorgit.plugins.bash.lang.psi.arithmetic;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.intellij.codeInsight.CodeInsightTestCase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Assert;

/**
 * @author jansorg
 */
public abstract class AbstractArithExprTest extends CodeInsightTestCase {
    protected ArithmeticExpression configureTopArithExpression() throws Exception {
        VirtualFile file = configure();
        setupCursorAndSelection(myEditor);

        PsiFile psi = getFile();

        PsiElement element = psi.findElementAt(getEditor().getCaretModel().getOffset());
        Assert.assertNotNull(element);

        element = PsiTreeUtil.getParentOfType(element, ArithmeticExpression.class);
        Assert.assertNotNull("No arithmetic expression found at the caret position", element);

        //find the top expression
        ArithmeticExpression start = (ArithmeticExpression) element;
        while (start.getParent() instanceof ArithmeticExpression) {
            start = (ArithmeticExpression) start.getParent();
        }

        return start;
    }

    protected VirtualFile configure() throws Exception {
        return configureByFile(getTestName(false) + ".bash", "");
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/arithmetic/";
    }
}
