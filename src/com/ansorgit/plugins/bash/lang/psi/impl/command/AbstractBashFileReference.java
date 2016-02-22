package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract file reference implementation to allow implementations for smart and dumb mode.
 *
 * @author jansorg
 */
abstract class AbstractBashFileReference extends CachingReference implements BashReference, BindablePsiReference {
    protected final AbstractBashCommand<?> cmd;

    public AbstractBashFileReference(AbstractBashCommand<?> cmd) {
        this.cmd = cmd;
    }

    @Override
    public String getReferencedName() {
        return cmd.getReferencedCommandName();
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
