package com.ansorgit.plugins.bash.lang.psi.stubs.api;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.util.io.StringRef;

/**
 * @author jansorg
 */
public interface BashFileStub extends PsiFileStub<BashFile> {
    StringRef getName();
}

