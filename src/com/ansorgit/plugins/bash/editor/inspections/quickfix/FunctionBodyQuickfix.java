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

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.lang.psi.api.BashBlock;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Wrap a function body in curly brackets.
 * <br>
 * @author jansorg
 */
public class FunctionBodyQuickfix extends AbstractBashPsiElementQuickfix {
    public FunctionBodyQuickfix(BashFunctionDef functionDef) {
        super(functionDef);
    }

    @NotNull
    public String getText() {
        return "Wrap function body in curly brackets";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        if (!CodeInsightUtilBase.prepareEditorForWrite(editor)) {
            return;
        }

        BashFunctionDef functionDef = (BashFunctionDef) startElement;

        final BashBlock block = functionDef.functionBody();
        if (block != null) {
            StringBuilder builder = new StringBuilder();
            final BashBlock body = functionDef.functionBody();
            builder.append("{ ").append(body.getText()).append(" }");

            int startOffset = body.getTextOffset();
            int endOffset = startOffset + body.getTextLength();

            Document document = PsiDocumentManager.getInstance(project).getDocument(file);
            document.replaceString(startOffset, endOffset, builder);

            PsiDocumentManager.getInstance(project).commitDocument(document);
        }
    }
}
