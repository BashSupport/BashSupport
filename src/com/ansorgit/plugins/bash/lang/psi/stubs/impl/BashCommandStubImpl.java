package com.ansorgit.plugins.bash.lang.psi.stubs.impl;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashCommandStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author ilyas
 */
public class BashCommandStubImpl extends StubBase<BashCommand> implements BashCommandStub {
    @Override
    public String getBashCommandFilename() {
        return StringRef.toString(bashCommandFilename);
    }

    private final StringRef bashCommandFilename;

    public BashCommandStubImpl(StubElement parent, StringRef bashCommandFilename, final IStubElementType elementType) {
        super(parent, elementType);
        this.bashCommandFilename = bashCommandFilename;
    }
}