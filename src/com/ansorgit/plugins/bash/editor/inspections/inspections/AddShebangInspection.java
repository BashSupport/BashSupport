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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.editor.inspections.SupressionUtil;
import com.ansorgit.plugins.bash.editor.inspections.quickfix.AddShebangQuickfix;
import com.ansorgit.plugins.bash.editor.inspections.quickfix.SupressAddShebangInspectionQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils.isSpecialBashFile;

/**
 * This inspection detects a missing shebang line and offers a file-level quickfix to add one.
 *
 * @author jansorg
 */
public class AddShebangInspection extends LocalInspectionTool implements CustomSuppressableInspectionTool, BatchSuppressableTool {
    public AddShebangInspection() {
    }

    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        PsiFile checkedFile = BashPsiUtils.findFileContext(file);

        if (checkedFile instanceof BashFile && !BashPsiUtils.isInjectedElement(file)
                && !isSpecialBashFile(checkedFile.getName())) {
            BashFile bashFile = (BashFile) checkedFile;
            Boolean isLanguageConsole = checkedFile.getUserData(BashFile.LANGUAGE_CONSOLE_MARKER);

            if ((isLanguageConsole == null || !isLanguageConsole) && !bashFile.hasShebangLine()) {
                return new ProblemDescriptor[]{
                        manager.createProblemDescriptor(checkedFile, "Add shebang line", new AddShebangQuickfix(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly)
                };
            }
        }

        return null;
    }

    @Nullable
    @Override
    public SuppressIntentionAction[] getSuppressActions(PsiElement element) {
        return SuppressIntentionActionFromFix.convertBatchToSuppressIntentionActions(getBatchSuppressActions(element));
    }

    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element) {
        PsiComment suppressionComment = SupressionUtil.findSuppressionComment(element);
        return suppressionComment != null && SupressionUtil.isSuppressionComment(suppressionComment, getID());
    }

    @NotNull
    @Override
    public SuppressQuickFix[] getBatchSuppressActions(@Nullable PsiElement element) {
        if (element != null && element.getContainingFile() instanceof BashFile) {
            return new SuppressQuickFix[]{
                    new SupressAddShebangInspectionQuickfix(AddShebangInspection.this.getID())
            };
        }

        return SuppressQuickFix.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitFile(BashFile file) {
                addDescriptors(checkFile(file, holder.getManager(), isOnTheFly));
            }

            private void addDescriptors(final ProblemDescriptor[] descriptors) {
                if (descriptors != null) {
                    for (ProblemDescriptor descriptor : descriptors) {
                        holder.registerProblem(descriptor);
                    }
                }
            }
        };
    }
}