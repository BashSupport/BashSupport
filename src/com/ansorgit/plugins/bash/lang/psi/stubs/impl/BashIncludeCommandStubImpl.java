package com.ansorgit.plugins.bash.lang.psi.stubs.impl;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashFunctionDefStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashIncludeCommandStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author ilyas
 */
public class BashIncludeCommandStubImpl extends StubBase<BashIncludeCommand> implements BashIncludeCommandStub {
    private final StringRef includedFilename;
    private final StringRef includerFilename;

    public BashIncludeCommandStubImpl(StubElement parent, StringRef includedFilename, StringRef included, final IStubElementType elementType) {
        super(parent, elementType);
        this.includedFilename = includedFilename;
        this.includerFilename = included;
    }

    @Override
    public String getIncludedFilename() {
        return StringRef.toString(includedFilename);
    }

    public String getIncluderFilename() {
        return StringRef.toString(includerFilename);
    }
}