package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashTrapCommand;
import com.ansorgit.plugins.bash.lang.psi.impl.BashKeywordDefaultImpl;
import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * Implements the Bash trap command.
 */
public class BashTrapCommandImpl extends BashKeywordDefaultImpl implements BashTrapCommand {
    public BashTrapCommandImpl() {
        super(BashTokenTypes.TRAP_KEYWORD);
    }

    public PsiElement keywordElement() {
        return findPsiChildByType(BashTokenTypes.TRAP_KEYWORD);
    }

    @Override
    public List<PsiElement> getSignalSpec() {
        return null;
    }

    @Override
    public PsiElement getCommandElement() {
        return null;
    }
}
