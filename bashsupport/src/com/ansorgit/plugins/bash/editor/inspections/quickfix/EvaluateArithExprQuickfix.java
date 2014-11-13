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
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
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
public class EvaluateArithExprQuickfix extends AbstractBashPsiElementQuickfix {
    private final String expressionText;
    private final long numericValue;

    public EvaluateArithExprQuickfix(ArithmeticExpression expression) {
        super(expression);
        this.expressionText = expression.getText();
        this.numericValue = expression.computeNumericValue();
    }

    @NotNull
    public String getText() {
        return "Replace '" + expressionText + "' with the result '" + numericValue + "'";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        TextRange r = startElement.getTextRange();
        String replacement = String.valueOf(numericValue);

        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document != null) {
            document.replaceString(r.getStartOffset(), r.getEndOffset(), replacement);
            PsiDocumentManager.getInstance(project).commitDocument(document);
        }
    }
}