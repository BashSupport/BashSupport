package com.ansorgit.plugins.bash.lang.psi.stubs.elements;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.stubs.BashFileStubBuilder;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashFileStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashFileStubImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashFullScriptNameIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashScriptNameIndex;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;

import java.io.IOException;

/**
 * @author ilyas
 */
public class BashStubFileElementType extends IStubFileElementType<BashFileStub> {
  private static final int CACHES_VERSION = 1;

  public BashStubFileElementType() {
    super(BashFileType.BASH_LANGUAGE);
  }

  public StubBuilder getBuilder() {
    return new BashFileStubBuilder();
  }

  @Override
  public int getStubVersion() {
    return super.getStubVersion() + CACHES_VERSION;
  }

  public String getExternalId() {
    return "bash.FILE";
  }

  @Override
  public void indexStub(PsiFileStub stub, IndexSink sink) {
    super.indexStub(stub, sink);
  }

  @Override
  public void serialize(final BashFileStub stub, final StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName().toString());
  }

  @Override
  public BashFileStub deserialize(final StubInputStream dataStream, final StubElement parentStub) throws IOException {
    StringRef name = dataStream.readName();
    return new BashFileStubImpl(name);
  }

  public void indexStub(BashFileStub stub, IndexSink sink) {
    String name = stub.getName().toString();
    if (name != null) {
      sink.occurrence(BashScriptNameIndex.KEY, name);
      sink.occurrence(BashFullScriptNameIndex.KEY, name);
    }
  }

}