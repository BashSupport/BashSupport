package com.ansorgit.plugins.bash.lang.psi.stubs.impl;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashVarStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author jansorg
 */
public class BashVarStubImpl extends StubBase<BashVar> implements BashVarStub {
    private final StringRef name;
    private int prefixLength;

    public BashVarStubImpl(StubElement parent, StringRef name, final IStubElementType elementType, int prefixLength) {
        super(parent, elementType);
        this.name = name;
        this.prefixLength = prefixLength;
    }

    public String getName() {
        return StringRef.toString(name);
    }

    @Override
    public int getPrefixLength() {
        return prefixLength;
    }
}