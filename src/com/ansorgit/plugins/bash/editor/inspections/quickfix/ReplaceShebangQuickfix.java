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

import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Replaces an existing, but invalid shebang command with a known command.
 * <br>
 * @author jansorg
 */
public class ReplaceShebangQuickfix extends AbstractBashPsiElementQuickfix {
    private final String command;
    private final TextRange replacementRange;

    public ReplaceShebangQuickfix(BashShebang shebang, String command) {
        this(shebang, command, null);
    }

    public ReplaceShebangQuickfix(BashShebang shebang, String command, @Nullable TextRange replacementRange) {
        super(shebang);
        this.command = command;
        this.replacementRange = replacementRange;
    }

    @NotNull
    public String getText() {
        return "Replace with '" + command + "'";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        BashShebang shebang = (BashShebang) startElement;

        shebang.updateCommand(command, replacementRange);
    }
}
