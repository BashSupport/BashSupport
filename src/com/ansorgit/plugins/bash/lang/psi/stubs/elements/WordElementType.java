package com.ansorgit.plugins.bash.lang.psi.stubs.elements;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashStubElementType;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionStub;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarUse;
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashWordImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashLanguageInjectionStubImpl;
import com.google.common.collect.Sets;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class WordElementType extends BashStubElementType<BashLanguageInjectionStub, BashLanguageInjectionHost> {
    public WordElementType() {
        super("word type");
    }

    @Override
    public BashLanguageInjectionHost createPsi(@NotNull BashLanguageInjectionStub stub) {
        return new BashWordImpl(stub, BashElementTypes.PARSED_WORD_ELEMENT);
    }

    @Override
    public BashLanguageInjectionStub createStub(@NotNull BashLanguageInjectionHost psi, StubElement parentStub) {
        boolean validBashHost = psi.isValidBashLanguageHost();

        Set<String> variableDefinitions = Collections.emptySet();
        Set<String> variableUses = Collections.emptySet();

        if (validBashHost) {
            variableDefinitions = Sets.newHashSet();
            for (BashVarDef varDef : psi.getVariableDefinitions()) {
                variableDefinitions.add(varDef.getName());
            }

            variableUses = Sets.newHashSet();
            for (BashVarUse varUse : psi.getVariableUses()) {
                variableUses.add(varUse.getName());
            }
        }

        return new BashLanguageInjectionStubImpl(parentStub, this, variableDefinitions, variableUses);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "bash.wordType";
    }

    @Override
    public void serialize(@NotNull BashLanguageInjectionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        Set<String> variableUses = stub.getVariableUses();
        dataStream.writeInt(variableUses.size());
        for (String var : variableUses) {
            dataStream.writeName(var);
        }

        Set<String> variableDefs = stub.getVariableDefinitions();
        dataStream.writeInt(variableDefs.size());
        for (String var : variableDefs) {
            dataStream.writeName(var);
        }
    }

    @NotNull
    @Override
    public BashLanguageInjectionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        Set<String> varUses = Sets.newHashSet();
        int varUseSize = dataStream.readInt();
        for (int i = 0; i < varUseSize; i++) {
            varUses.add(StringRef.toString(dataStream.readName()));
        }

        Set<String> varDefs = Sets.newHashSet();
        int varDefsSize = dataStream.readInt();
        for (int i = 0; i < varDefsSize; i++) {
            varDefs.add(StringRef.toString(dataStream.readName()));
        }

        return new BashLanguageInjectionStubImpl(parentStub, this, varDefs, varUses);
    }

    @Override
    public void indexStub(@NotNull BashLanguageInjectionStub stub, @NotNull IndexSink sink) {
        StubSharedImpl.indexVarDefinitions(stub, sink);

        StubSharedImpl.indexVarUses(stub, sink);
    }
}
