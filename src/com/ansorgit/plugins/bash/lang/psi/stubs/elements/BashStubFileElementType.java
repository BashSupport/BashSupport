package com.ansorgit.plugins.bash.lang.psi.stubs.elements;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.stubs.BashFileStubBuilder;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashFileStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashFileStubImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashFullScriptNameIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIndexVersion;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashScriptNameIndex;
import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BashStubFileElementType extends IStubFileElementType<BashFileStub> {

    public BashStubFileElementType() {
        super(BashFileType.BASH_LANGUAGE);
    }

    @Override
    public StubBuilder getBuilder() {
        return new DefaultStubBuilder() {
            @NotNull
            @Override
            protected StubElement createStubForFile(@NotNull PsiFile file) {
                if (file instanceof BashFile) {
                    return new BashFileStubImpl((BashFile) file);
                }

                return super.createStubForFile(file);
            }
        };
    }

    @Override
    public int getStubVersion() {
        return super.getStubVersion() + BashIndexVersion.CACHES_VERSION;
    }

    @NotNull
    public String getExternalId() {
        return "bash.FILE";
    }

    @Override
    public void indexStub(@NotNull PsiFileStub stub, @NotNull IndexSink sink) {
        super.indexStub(stub, sink);
        assert stub instanceof BashFileStub;

        String name = ((BashFileStub) stub).getName().toString();
        sink.occurrence(BashScriptNameIndex.KEY, name);
        sink.occurrence(BashFullScriptNameIndex.KEY, name);
    }

    @Override
    public void serialize(@NotNull final BashFileStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName().toString());
    }

    @NotNull
    @Override
    public BashFileStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        return new BashFileStubImpl(null, name);
    }
}