package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: 18.02.11
 * Time: 20:17
 */
public class BashIncludeCommandImpl extends BashCommandImpl implements BashIncludeCommand {
    public BashIncludeCommandImpl(ASTNode astNode) {
        super(astNode, "Bash include command");
    }

    @NotNull
    public BashFileReference getFileReference() {
        PsiElement[] children = getChildren();
        if (children.length == 0 || !(children[0] instanceof BashFileReference)) {
            throw new IllegalStateException("File reference not found");
        }

        return (BashFileReference) children[0];
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitIncludeCommand(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public boolean isIncludeCommand() {
        return true;
    }

    @Override
    public boolean isFunctionCall() {
        return false;
    }

    @Override
    public boolean isInternalCommand() {
        return true;
    }

    @Override
    public boolean isExternalCommand() {
        return false;
    }

    @Override
    public boolean isPureAssignment() {
        return false;
    }

    @Override
    public boolean isVarDefCommand() {
        return false;
    }

    @Override
    public boolean canNavigate() {
        return canNavigateToSource();
    }

    @Override
    public boolean canNavigateToSource() {
        return getFileReference().findReferencedFile() != null;
    }
}
