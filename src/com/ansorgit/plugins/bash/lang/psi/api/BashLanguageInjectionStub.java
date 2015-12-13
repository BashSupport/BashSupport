package com.ansorgit.plugins.bash.lang.psi.api;

import com.intellij.psi.stubs.StubElement;

import java.util.Set;

public interface BashLanguageInjectionStub extends StubElement<BashLanguageInjectionHost> {
    Set<String> getVariableUses();

    Set<String> getVariableDefinitions();
}
