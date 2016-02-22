package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

/**
 * File reference implementation to be used in dumb mode and for scratch files, it resolved withot index access.
 *
 * @author jansorg
 */
class DumbBashFileReference extends AbstractBashFileReference {
    public DumbBashFileReference(AbstractBashCommand<?> cmd) {
        super(cmd);
    }

    @Nullable
    @Override
    public PsiElement resolveInner() {
        final String referencedName = cmd.getReferencedCommandName();
        if (referencedName == null) {
            return null;
        }

        PsiFile currentFile = cmd.getContainingFile();
        return BashPsiFileUtils.findRelativeFile(currentFile, referencedName);
    }
}
