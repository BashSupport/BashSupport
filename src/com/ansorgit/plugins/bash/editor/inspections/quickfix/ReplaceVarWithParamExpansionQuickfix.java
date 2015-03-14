/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: ReplaceVarWithParamExpansionQuickfix.java, Class: ReplaceVarWithParamExpansionQuickfix
 * Last modified: 2013-04-30
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ReadOnlyFragmentModificationException;
import com.intellij.openapi.editor.ReadOnlyModificationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * This quickfix replaces a simple variable usage with the equivalent parameter expansion form.
 * <p/>
 * User: jansorg
 * Date: 28.12.10
 * Time: 12:19
 */
public class ReplaceVarWithParamExpansionQuickfix extends AbstractBashPsiElementQuickfix {
    private final String variableName;

    public ReplaceVarWithParamExpansionQuickfix(BashVar var) {
        super(var);
        this.variableName = var.getReference().getReferencedName();
    }

    public static boolean isAvailableAt(@NotNull Project project, @NotNull PsiFile file, @NotNull BashVar var) {
        if (BashPsiUtils.hasParentOfType(var, BashString.class, 4) || BashPsiUtils.hasParentOfType(var, ArithmeticExpression.class, 4)) {
            return false;
        }

        return !var.isParameterExpansion() && !var.isBuiltinVar();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        return startElement instanceof BashVar && isAvailableAt(project, file, (BashVar) startElement);
    }

    @NotNull
    public String getText() {
        if (variableName.length() > 10) {
            return "Replace with '${...}'";
        }

        return String.format("Replace '%s' with '${%s}'", variableName, variableName);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        TextRange textRange = startElement.getTextRange();

        try {
            // Replace this position with the same value, we have to trigger a reparse somehow
            Document document = file.getViewProvider().getDocument();
            if (document != null && document.isWritable()) {
                document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), "${" + variableName + "}");
                PsiDocumentManager.getInstance(project).commitDocument(document);
            }
        } catch (ReadOnlyModificationException e) {
            //ignore
        } catch (ReadOnlyFragmentModificationException e) {
            //ignore
        }
    }
}
