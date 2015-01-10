/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: EvaluateExpansionQuickfix.java, Class: EvaluateExpansionQuickfix
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

import com.ansorgit.plugins.bash.lang.psi.api.word.BashExpansion;
import com.ansorgit.plugins.bash.lang.valueExpansion.ValueExpansionUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Evaluates an expansion and replaces the placeholder with the evaluated result.
 * <p/>
 * User: jansorg
 * Date: Nov 15, 2009
 * Time: 12:50:35 AM
 */
public class EvaluateExpansionQuickfix extends AbstractBashPsiElementQuickfix {
    private final boolean enableBash4;
    private final String expansionDef;

    public EvaluateExpansionQuickfix(BashExpansion expansion, boolean enableBash4) {
        super(expansion);
        this.enableBash4 = enableBash4;
        this.expansionDef = expansion.getText();
    }

    @NotNull
    public String getText() {
        String replacement = ValueExpansionUtil.expand(expansionDef, enableBash4);

        if (replacement.length() < 20) {
            return "Replace with the result '" + replacement + "'";
        }

        return "Replace with evaluated expansion";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        TextRange r = startElement.getTextRange();

        Document document = PsiDocumentManager.getInstance(project).getDocument(file);

        String replacement = ValueExpansionUtil.expand(startElement.getText(), enableBash4);
        if (replacement != null && document != null) {
            editor.getDocument().replaceString(r.getStartOffset(), r.getEndOffset(), replacement);
            PsiDocumentManager.getInstance(project).commitDocument(document);
        }
    }
}
