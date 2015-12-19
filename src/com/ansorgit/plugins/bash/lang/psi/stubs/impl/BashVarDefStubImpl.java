package com.ansorgit.plugins.bash.lang.psi.stubs.impl;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashVarDefStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author jansorg
 */
public class BashVarDefStubImpl extends StubBase<BashVarDef> implements BashVarDefStub {
    private final StringRef name;
    private boolean readOnly;

    public BashVarDefStubImpl(StubElement parent, StringRef name, final IStubElementType elementType, boolean readOnly) {
        super(parent, elementType);
        this.name = name;
        this.readOnly = readOnly;
    }

    public String getName() {
        return StringRef.toString(name);
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }
}