package com.ansorgit.plugins.bash.lang.psi.stubs.impl;

import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionStub;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;

import java.util.Set;

public class BashLanguageInjectionStubImpl extends StubBase<BashLanguageInjectionHost> implements BashLanguageInjectionStub {
    private final Set<String> variableDefinitions;
    private final Set<String> variableUses;

    public BashLanguageInjectionStubImpl(StubElement parent, IStubElementType<BashLanguageInjectionStub, BashLanguageInjectionHost> elementType, Set<String> variableDefinitions, Set<String> variableUses) {
        super(parent, elementType);
        this.variableDefinitions = variableDefinitions;
        this.variableUses = variableUses;
    }

    @Override
    public Set<String> getVariableUses() {
        return variableUses;
    }

    @Override
    public Set<String> getVariableDefinitions() {
        return variableDefinitions;
    }
}
