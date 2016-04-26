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

package com.ansorgit.plugins.bash.lang.psi.impl.refactoring;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Handles element manipulation of Bash function and file references.
 */
public class BashCommandManipulator implements ElementManipulator<BashCommand> {
    @Override
    public BashCommand handleContentChange(@NotNull BashCommand cmd, @NotNull TextRange textRange, String newElementName) throws IncorrectOperationException {
        if (StringUtil.isEmpty(newElementName)) {
            throw new IncorrectOperationException("Can not handle empty names");
        }

        PsiElement commandElement = cmd.commandElement();
        if (commandElement == null) {
            throw new IncorrectOperationException("invalid command");
        }

        BashGenericCommand replacement = BashPsiElementFactory.createCommand(cmd.getProject(), newElementName);
        BashPsiUtils.replaceElement(commandElement, replacement);

        return cmd;
    }

    @Override
    public BashCommand handleContentChange(@NotNull final BashCommand element, final String newContent) throws IncorrectOperationException {
        return handleContentChange(element, TextRange.create(0, element.getTextLength()), newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull BashCommand cmd) {
        final PsiElement element = cmd.commandElement();
        if (element == null) {
            return TextRange.from(0, cmd.getTextLength());
        }

        return TextRange.from(element.getStartOffsetInParent(), element.getTextLength());
    }
}
