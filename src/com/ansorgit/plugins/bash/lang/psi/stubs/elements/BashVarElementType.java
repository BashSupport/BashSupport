/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.psi.stubs.elements;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashStubElementType;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashVarStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashVarStubImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashVarIndex;
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
public class BashVarElementType extends BashStubElementType<BashVarStub, BashVar> {
    public BashVarElementType() {
        super("var-use-element");
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "bash.var";
    }

    public void serialize(@NotNull BashVarStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeInt(stub.getPrefixLength());
    }

    @NotNull
    public BashVarStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef ref = dataStream.readName();
        int prefixLength = dataStream.readInt();

        return new BashVarStubImpl(parentStub, ref, this, prefixLength);
    }

    public BashVar createPsi(@NotNull BashVarStub stub) {
        return new BashVarImpl(stub, BashElementTypes.VAR_ELEMENT);
    }

    public BashVarStub createStub(@NotNull BashVar psi, StubElement parentStub) {
        return new BashVarStubImpl(parentStub, StringRef.fromString(psi.getName()), BashElementTypes.VAR_ELEMENT, psi.getPrefixLength());
    }

    @Override
    public void indexStub(@NotNull BashVarStub stub, @NotNull IndexSink sink) {
        final String name = stub.getName();
        if (name != null) {
            sink.occurrence(BashVarIndex.KEY, name);
        }
    }
}
