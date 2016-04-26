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
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.impl.command.BashSimpleCommandImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashCommandStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashCommandStubImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashCommandNameIndex;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.PathUtilRt;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author jansorg
 */
public class BashSimpleCommandElementType extends BashStubElementType<BashCommandStub, BashCommand> {
    public BashSimpleCommandElementType() {
        super("simple-command");
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "bash.simpleCommand";
    }

    public void serialize(@NotNull BashCommandStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getBashCommandName());
        dataStream.writeBoolean(stub.isInternalCommand(false));
        dataStream.writeBoolean(stub.isInternalCommand(true));
        dataStream.writeBoolean(stub.isGenericCommand());
    }

    @NotNull
    public BashCommandStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef bashCommandFilename = dataStream.readName();
        boolean internalCommandBash3 = dataStream.readBoolean();
        boolean internalCommandBash4 = dataStream.readBoolean();
        boolean genericCommand = dataStream.readBoolean();

        return new BashCommandStubImpl(parentStub, StringRef.toString(bashCommandFilename), this, internalCommandBash3, internalCommandBash4, genericCommand);
    }

    public BashCommand createPsi(@NotNull BashCommandStub stub) {
        return new BashSimpleCommandImpl(stub, BashElementTypes.SIMPLE_COMMAND_ELEMENT, "simple command");
    }

    public BashCommandStub createStub(@NotNull BashCommand psi, StubElement parentStub) {
        String filename = null;

        String commandName = psi.getReferencedCommandName();
        if (commandName != null) {
            filename = PathUtilRt.getFileName(commandName);
        }

        return new BashCommandStubImpl(parentStub, filename, BashElementTypes.SIMPLE_COMMAND_ELEMENT, psi.isInternalCommand(false), psi.isInternalCommand(true), psi.isGenericCommand());
    }

    @Override
    public void indexStub(@NotNull BashCommandStub stub, @NotNull IndexSink sink) {
        final String filename = stub.getBashCommandName();
        if (filename != null) {
            sink.occurrence(BashCommandNameIndex.KEY, filename);
        }
    }
}
