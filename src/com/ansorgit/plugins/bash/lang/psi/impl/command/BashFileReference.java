package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashSearchScopes;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reference to another file, used in include commands. This implementation handles the dumb mode and turns off index
 * access during dumb  mode operations.
 *
 * @author jansorg
 */
class BashFileReference extends CachingReference implements BashReference, BindablePsiReference {
    private final AbstractBashCommand<?> cmd;

    public BashFileReference(AbstractBashCommand<?> cmd) {
        this.cmd = cmd;
    }

    @Override
    public String getReferencedName() {
        return cmd.getReferencedCommandName();
    }

    @Nullable
    @Override
    public PsiElement resolveInner() {
        final String referencedName = cmd.getReferencedCommandName();
        if (referencedName == null) {
            return null;
        }

        if (!DumbService.isDumb(cmd.getProject())) {
            String fileName = PathUtil.getFileName(referencedName);
            GlobalSearchScope scope = BashSearchScopes.moduleScope(cmd.getContainingFile());
            PsiFileSystemItem[] files = FilenameIndex.getFilesByName(cmd.getProject(), fileName, scope, false);
            if (files.length == 0) {
                return null;
            }
        }

        PsiFile currentFile = cmd.getContainingFile();
        return BashPsiFileUtils.findRelativeFile(currentFile, referencedName);
    }

    @Override
    public PsiElement getElement() {
        return cmd;
    }

    @Override
    public TextRange getRangeInElement() {
        return getManipulator().getRangeInElement(cmd);
    }

    @NotNull
    private ElementManipulator<AbstractBashCommand<?>> getManipulator() {
        ElementManipulator<AbstractBashCommand<?>> manipulator = ElementManipulators.<AbstractBashCommand<?>>getManipulator(cmd);
        if (manipulator == null) {
            throw new IncorrectOperationException("No element manipulator found for " + cmd);
        }

        return manipulator;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        String referencedName = cmd.getReferencedCommandName();
        return referencedName != null ? referencedName : "";
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return getManipulator().handleContentChange(cmd, newElementName);
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (element instanceof PsiFile) {
            //findRelativeFilePath already leaves the injection host file
            PsiFile currentFile = cmd.getContainingFile();
            String relativeFilePath = BashPsiFileUtils.findRelativeFilePath(currentFile, (PsiFile) element);

            return handleElementRename(relativeFilePath);
        }

        throw new IncorrectOperationException("unsupported for element " + element);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
