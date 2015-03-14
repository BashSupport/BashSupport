/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: EvaluateArithExprQuickfix.java, Class: EvaluateArithExprQuickfix
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Replaces a static arithmetic expression with the evaluated result.
 * <p/>
 * User: jansorg
 * Date: Nov 15, 2009
 * Time: 12:50:35 AM
 */
public class EvaluateArithExprQuickfix extends AbstractBashQuickfix {
    private final ArithmeticExpression expression;

    public EvaluateArithExprQuickfix(ArithmeticExpression expression) {
        this.expression = expression;
    }

    @NotNull
    public String getName() {
        return "Replace '" + expression.getText() + "' with the result '" + expression.computeNumericValue() + "'";
    }

    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        TextRange r = expression.getTextRange();
        String replacement = String.valueOf(expression.computeNumericValue());
        editor.getDocument().replaceString(r.getStartOffset(), r.getEndOffset(), replacement);
    }
}