package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

public class BashArithmeticCommandImpl extends BashBaseElement implements PsiElement {

    public BashArithmeticCommandImpl(ASTNode astNode) {
        super(astNode, "Arithmetic command");
    }

}
