package com.ansorgit.plugins.bash.lang.psi.stubs.api;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.intellij.psi.stubs.NamedStub;

public interface BashVarStub extends NamedStub<BashVar> {
    int getPrefixLength();
}
