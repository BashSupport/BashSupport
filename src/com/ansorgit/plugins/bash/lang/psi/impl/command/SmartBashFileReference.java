package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashSearchScopes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Reference to another file, used in include commands. This implementation handles the dumb mode and turns off index
 * access during dumb  mode operations.
 *
 * @author jansorg
 */
class SmartBashFileReference extends AbstractBashFileReference {

    public SmartBashFileReference(AbstractBashCommand<?> cmd) {
        super(cmd);
    }

    @Nullable
    @Override
    public PsiElement resolveInner() {
        final String referencedName = cmd.getReferencedCommandName();
        if (referencedName == null) {
            return null;
        }

        String fileName = PathUtil.getFileName(referencedName);
        GlobalSearchScope scope = BashSearchScopes.moduleScope(cmd.getContainingFile());

        PsiFileSystemItem[] files = FilenameIndex.getFilesByName(cmd.getProject(), fileName, scope, false);
        if (files.length == 0) {
            return null;
        }

        PsiFile currentFile = cmd.getContainingFile();
        return BashPsiFileUtils.findRelativeFile(currentFile, referencedName);
    }

}
