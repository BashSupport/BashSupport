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
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.impl.command.BashIncludeCommandImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashIncludeCommandStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashIncludeCommandStubImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludeCommandIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludedFilenamesIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.indexing.IndexingDataKeys;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author jansorg
 */
public class BashIncludeCommandElementType extends BashStubElementType<BashIncludeCommandStub, BashIncludeCommand> {
    public BashIncludeCommandElementType() {
        super("include-command");
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "bash.includeCommand";
    }

    public void serialize(@NotNull BashIncludeCommandStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getIncludedFilename());
        dataStream.writeName(stub.getIncluderFilePath());
    }

    @NotNull
    public BashIncludeCommandStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef filename = dataStream.readName();
        StringRef includer = dataStream.readName();
        return new BashIncludeCommandStubImpl(parentStub, filename, includer, this);
    }

    public BashIncludeCommand createPsi(@NotNull BashIncludeCommandStub stub) {
        return new BashIncludeCommandImpl(stub, BashElementTypes.INCLUDE_COMMAND_ELEMENT);
    }

    public BashIncludeCommandStub createStub(@NotNull BashIncludeCommand psi, StubElement parentStub) {
        BashFileReference fileReference = psi.getFileReference();

        String filename = null;
        String includer = null;

        if (fileReference != null && fileReference.isStatic()) {
            filename = fileReference.getFilename();
            if (filename.contains("/") && !filename.endsWith("/")) {
                int index = filename.lastIndexOf("/");
                filename = filename.substring(index + 1);
            }

            VirtualFile virtualFile = psi.getContainingFile().getUserData(IndexingDataKeys.VIRTUAL_FILE);
            if (virtualFile == null) {
                virtualFile = psi.getContainingFile().getViewProvider().getVirtualFile();
            }

            includer = virtualFile.getPath();
        }

        return new BashIncludeCommandStubImpl(parentStub, StringRef.fromString(filename), StringRef.fromString(includer), BashElementTypes.INCLUDE_COMMAND_ELEMENT);
    }

    @Override
    public void indexStub(@NotNull BashIncludeCommandStub stub, @NotNull IndexSink sink) {
        final String filenamef = stub.getIncludedFilename();
        if (filenamef != null) {
            sink.occurrence(BashIncludedFilenamesIndex.KEY, filenamef);
        }

        String includerFilePath = stub.getIncluderFilePath();
        if (includerFilePath != null) {
            sink.occurrence(BashIncludeCommandIndex.KEY, includerFilePath);
        }
    }
}
