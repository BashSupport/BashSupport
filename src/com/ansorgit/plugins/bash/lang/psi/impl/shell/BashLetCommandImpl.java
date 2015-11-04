package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashLetCommand;
import com.ansorgit.plugins.bash.lang.psi.impl.BashKeywordDefaultImpl;
import com.intellij.psi.PsiElement;

/**
 * Implementation of the let command.
 *
 * @author jansorg
 */
public class BashLetCommandImpl extends BashKeywordDefaultImpl implements BashLetCommand {
    public BashLetCommandImpl() {
        super(BashElementTypes.LET_COMMAND);
    }

    public PsiElement keywordElement() {
        return findPsiChildByType(BashTokenTypes.LET_KEYWORD);
    }


}
