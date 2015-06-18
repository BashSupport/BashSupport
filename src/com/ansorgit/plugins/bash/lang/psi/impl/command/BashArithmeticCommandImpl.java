package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;

public class BashArithmeticCommandImpl extends AbstractBashCommand<StubElement> implements PsiElement {

    public BashArithmeticCommandImpl(ASTNode astNode) {
        super(astNode, "Arithmetic command");
    }

}
