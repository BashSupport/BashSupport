package com.ansorgit.plugins.bash.lang.psi.stubs.impl;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashCommandStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;

/**
 * @author ilyas
 */
public class BashCommandStubImpl extends StubBase<BashCommand> implements BashCommandStub {
    private final String bashCommandFilename;
    private boolean internalCommandBash4;
    private boolean internalCommandBash3;
    private boolean genericCommand;

    @Override
    public boolean isGenericCommand() {
        return genericCommand;
    }

    public BashCommandStubImpl(StubElement parent, String bashCommandFilename, final IStubElementType elementType, boolean internalCommandBash3, boolean internalCommandBash4, boolean genericCommand) {
        super(parent, elementType);

        this.bashCommandFilename = bashCommandFilename;
        this.internalCommandBash3 = internalCommandBash3;
        this.internalCommandBash4 = internalCommandBash4;
        this.genericCommand = genericCommand;
    }

    @Override
    public String getBashCommandName() {
        return bashCommandFilename;
    }

    @Override
    public boolean isInternalCommand(boolean bash4) {
        return bash4 ? internalCommandBash4 : internalCommandBash3;
    }
}