package com.ansorgit.plugins.bash.lang.psi.stubs.elements;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashStubElementType;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.function.BashFunctionDefImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarDefImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashFunctionDefStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashVarDefStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashFunctionDefStubImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashVarDefStubImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashFunctionNameIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashVarDefIndex;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

import java.io.IOException;

/**
 * @author jansorg
 */
public class BashVarDefElementType extends BashStubElementType<BashVarDefStub, BashVarDef> {
    public BashVarDefElementType() {
        super("var-def-element");
    }

    public void serialize(BashVarDefStub stub, StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    public BashVarDefStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef ref = dataStream.readName();
        return new BashVarDefStubImpl(parentStub, ref, this);
    }

    public PsiElement createElement(ASTNode node) {
        return new BashFunctionDefImpl(node);
    }

    public BashVarDef createPsi(BashVarDefStub stub) {
        return new BashVarDefImpl(stub, BashElementTypes.VAR_DEF_ELEMENT);
    }

    public BashVarDefStub createStub(BashVarDef psi, StubElement parentStub) {
        return new BashVarDefStubImpl(parentStub, StringRef.fromString(psi.getName()), BashElementTypes.VAR_DEF_ELEMENT);
    }

    @Override
    public void indexStub(BashVarDefStub stub, IndexSink sink) {
        final String name = stub.getName();
        if (name != null) {
            sink.occurrence(BashVarDefIndex.KEY, name);
        }
    }
}
