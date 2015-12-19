package com.ansorgit.plugins.bash.lang.psi.stubs.elements;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashStubElementType;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarDefImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashVarDefStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashVarDefStubImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashVarDefIndex;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author jansorg
 */
public class BashVarDefElementType extends BashStubElementType<BashVarDefStub, BashVarDef> {
    public BashVarDefElementType() {
        super("var-def-element");
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "bash.varDef";
    }

    public void serialize(@NotNull BashVarDefStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeBoolean(stub.isReadOnly());
    }

    @NotNull
    public BashVarDefStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef ref = dataStream.readName();
        boolean readOnly = dataStream.readBoolean();

        return new BashVarDefStubImpl(parentStub, ref, this, readOnly);
    }

    public BashVarDef createPsi(@NotNull BashVarDefStub stub) {
        return new BashVarDefImpl(stub, BashElementTypes.VAR_DEF_ELEMENT);
    }

    public BashVarDefStub createStub(@NotNull BashVarDef psi, StubElement parentStub) {
        return new BashVarDefStubImpl(parentStub, StringRef.fromString(psi.getName()), BashElementTypes.VAR_DEF_ELEMENT, psi.isReadonly());
    }

    @Override
    public void indexStub(@NotNull BashVarDefStub stub, @NotNull IndexSink sink) {
        final String name = stub.getName();
        if (name != null) {
            sink.occurrence(BashVarDefIndex.KEY, name);
        }
    }
}
