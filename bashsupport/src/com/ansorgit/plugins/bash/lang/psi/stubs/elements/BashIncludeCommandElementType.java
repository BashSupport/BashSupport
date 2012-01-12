package com.ansorgit.plugins.bash.lang.psi.stubs.elements;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashStubElementType;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.impl.command.BashIncludeCommandImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.function.BashFunctionDefImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashIncludeCommandStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashIncludeCommandStubImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludeCommandIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludedFilenamesIndex;
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
public class BashIncludeCommandElementType extends BashStubElementType<BashIncludeCommandStub, BashIncludeCommand> {
    public BashIncludeCommandElementType() {
        super("include-command");
    }

    public void serialize(BashIncludeCommandStub stub, StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getIncludedFilename());
        dataStream.writeName(stub.getIncluderFilename());
    }

    public BashIncludeCommandStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef filename = dataStream.readName();
        StringRef includer = dataStream.readName();
        return new BashIncludeCommandStubImpl(parentStub, filename, includer, this);
    }

    public PsiElement createElement(ASTNode node) {
        return new BashFunctionDefImpl(node);
    }

    public BashIncludeCommand createPsi(BashIncludeCommandStub stub) {
        return new BashIncludeCommandImpl(stub, BashElementTypes.INCLUDE_COMMAND_ELEMENT);
    }

    public BashIncludeCommandStub createStub(BashIncludeCommand psi, StubElement parentStub) {
        BashFileReference fileReference = psi.getFileReference();

        String filename = null;
        String includer = null;

        if (fileReference.isStatic()) {
            filename = psi.getFileReference().getFilename();
            includer = psi.getContainingFile().getName();
        }

        return new BashIncludeCommandStubImpl(parentStub, StringRef.fromString(filename), StringRef.fromString(includer), BashElementTypes.INCLUDE_COMMAND_ELEMENT);
    }

    @Override
    public void indexStub(BashIncludeCommandStub stub, IndexSink sink) {
        final String filenamef = stub.getIncludedFilename();
        if (filenamef != null) {
            sink.occurrence(BashIncludedFilenamesIndex.KEY, filenamef);
        }

        String includerFilename = stub.getIncluderFilename();
        if (includerFilename != null) {
            sink.occurrence(BashIncludeCommandIndex.KEY, includerFilename);
        }
    }
}
