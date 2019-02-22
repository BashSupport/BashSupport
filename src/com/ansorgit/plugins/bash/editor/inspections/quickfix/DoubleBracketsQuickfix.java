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

import com.ansorgit.plugins.bash.editor.inspections.BashInspections;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashConditionalCommand;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Replaces a test command [ ... ] with the extended test command [[ ... ]].
 */
public class DoubleBracketsQuickfix extends LocalQuickFixAndIntentionActionOnPsiElement {
    public DoubleBracketsQuickfix(BashConditionalCommand conditionalCommand) {
        super(conditionalCommand);
    }

    @NotNull
    public String getText() {
        return "Replace with double brackets";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return BashInspections.FAMILY_NAME;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            // builds 163.x set the read-only flag for unknown reasons, work around it in tests
            return true;
        }

        Document document = file.getViewProvider().getDocument();
        return document != null && document.isWritable();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        BashConditionalCommand conditionalCommand = (BashConditionalCommand) startElement;
        String command = conditionalCommand.getCommandText();
        PsiElement replacement = useDoubleBrackets(project, command);
        startElement.replace(replacement);
    }

    private static PsiElement useDoubleBrackets(Project project, String command) {
        String newCommand = "[[" + command + "]]";
        for (Replacement replacement : Replacement.values()) {
            newCommand = replacement.apply(newCommand);
        }

        PsiFile dummyBashFile = BashPsiElementFactory.createDummyBashFile(project, newCommand);
        return dummyBashFile.getFirstChild();
    }

    private enum Replacement {
        AND("(?<!\\S)-a(?!\\S)", "&&"),
        OR("(?<!\\S)-o(?!\\S)", "||"),
        LESS_THAN("\\\\<", "<"),
        MORE_THAN("\\\\>", ">"),
        LEFT_PARANTHESIS("\\\\\\(", "("),
        RIGHT_PARANTHESIS("\\\\\\)", ")");

        private final Pattern regex;
        private final String replacement;

        Replacement(String regex, String replacement) {
            this.regex = Pattern.compile(regex);
            this.replacement = replacement;
        }

        public String apply(CharSequence input) {
            return regex.matcher(input).replaceAll(replacement);
        }
    }
}
