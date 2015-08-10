package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashTrapCommand;
import com.ansorgit.plugins.bash.lang.psi.impl.BashKeywordDefaultImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;

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
    public PsiElement getSignalHandlerElement() {
        //the first element after the "trap" command element
        PsiElement firstParam = BashPsiUtils.findNextSibling(getFirstChild(), BashTokenTypes.WHITESPACE);
        if (firstParam == null) {
            return null;
        }

        String text = firstParam.getText();
        if (text.startsWith("-p") || text.startsWith("-l")) {
            return null;
        }

        //the string/word container is embedded in a structure of "simple command" -> "generic command element"
        //extract it without relying too much on the defined structure
        PsiElement child = firstParam;
        while (child.getTextRange().equals(firstParam.getTextRange())) {
            PsiElement firstChild = child.getFirstChild();
            if (firstChild == null || firstChild instanceof LeafPsiElement) {
                break;
            }

            child = child.getFirstChild();
        }

        return child;
    }
}
