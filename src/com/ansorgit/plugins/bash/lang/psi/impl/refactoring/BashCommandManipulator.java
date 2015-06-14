package com.ansorgit.plugins.bash.lang.psi.impl.refactoring;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Handles element manipulation of Bash function references.
 */
public class BashCommandManipulator extends AbstractElementManipulator<BashCommand> {
    @Override
    public BashCommand handleContentChange(@NotNull BashCommand cmd, @NotNull TextRange textRange, String newElementName) throws IncorrectOperationException {
        if (StringUtil.isEmpty(newElementName)) {
            return null;
        }

        final PsiElement original = cmd.commandElement();
        final PsiElement replacement = BashPsiElementFactory.createWord(cmd.getProject(), newElementName);

        //fixme handled psi replacement properly?
        cmd.getNode().replaceChild(original.getNode(), replacement.getNode());
        return cmd;
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
