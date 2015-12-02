package com.ansorgit.plugins.bash.lang.psi.stubs.impl;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashIncludeCommandStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

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

    public String getIncluderFilePath() {
        return StringRef.toString(includerFilename);
    }

    @Override
    public boolean isGenericCommand() {
        return false;
    }

    @Override
    public boolean isInternalCommand(boolean bash4) {
        return true;
    }
}