package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.intellij.psi.stubs.IStubElementType;
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

    @NotNull
    public String getExternalId() {
        return "bash." + super.toString();
    }
}
