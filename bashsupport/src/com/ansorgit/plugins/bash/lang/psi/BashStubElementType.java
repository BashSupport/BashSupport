package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author jansorg
 */
public abstract class BashStubElementType<S extends StubElement, T extends BashPsiElement> extends IStubElementType<S, T> {
    public BashStubElementType(@NonNls @NotNull String debugName) {
        super(debugName, BashFileType.BASH_LANGUAGE);
    }

    public abstract PsiElement createElement(final ASTNode node);

    public void indexStub(final S stub, final IndexSink sink) {
    }

    public String getExternalId() {
        return "bash." + super.toString();
    }
}
