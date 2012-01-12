package com.ansorgit.plugins.bash.lang.psi.stubs;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashFileStubImpl;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: 11.01.12
 * Time: 23:32
 */
public class BashFileStubBuilder extends DefaultStubBuilder {
    protected StubElement createStubForFile(@NotNull final PsiFile file) {
        if (file instanceof BashFile) {
            return new BashFileStubImpl((BashFile) file);
        }

        return super.createStubForFile(file);
    }

}
