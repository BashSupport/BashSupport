package com.ansorgit.plugins.bash.lang.psi.eval;

import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

public class BashEvalBlock extends BashBaseElement {
    public BashEvalBlock(ASTNode node) {
        super(node, "eval block");
    }

}
