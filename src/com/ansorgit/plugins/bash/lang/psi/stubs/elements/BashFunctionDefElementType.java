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
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.impl.function.BashFunctionDefImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashFunctionDefStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashFunctionDefStubImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashFunctionNameIndex;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author ilyas
 */
public class BashFunctionDefElementType extends BashStubElementType<BashFunctionDefStub, BashFunctionDef> {

    public BashFunctionDefElementType() {
        super("function-def-element");
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "bash.functionDef";
    }

    public void serialize(@NotNull BashFunctionDefStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    public BashFunctionDefStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef ref = dataStream.readName();
        return new BashFunctionDefStubImpl(parentStub, ref, this);
    }

    public BashFunctionDef createPsi(@NotNull BashFunctionDefStub stub) {
        return new BashFunctionDefImpl(stub, BashElementTypes.FUNCTION_DEF_COMMAND);
    }

    public BashFunctionDefStub createStub(@NotNull BashFunctionDef psi, StubElement parentStub) {
        return new BashFunctionDefStubImpl(parentStub, StringRef.fromString(psi.getName()), BashElementTypes.FUNCTION_DEF_COMMAND);
    }

    @Override
    public void indexStub(@NotNull BashFunctionDefStub stub, @NotNull IndexSink sink) {
        final String name = stub.getName();
        if (name != null) {
            sink.occurrence(BashFunctionNameIndex.KEY, name);
        }
    }
}
