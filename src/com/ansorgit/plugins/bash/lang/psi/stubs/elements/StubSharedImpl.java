package com.ansorgit.plugins.bash.lang.psi.stubs.elements;

import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashInjectionVarDefinitionsIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashInjectionVarUsesIndex;
import com.intellij.psi.stubs.IndexSink;

/**
 * @author jansorg
 */
class StubSharedImpl {
    public static void indexVarUses(BashLanguageInjectionStub stub, IndexSink sink) {
        for (String varName : stub.getVariableUses()) {
            sink.occurrence(BashInjectionVarUsesIndex.KEY, varName);
        }
    }

    public static void indexVarDefinitions(BashLanguageInjectionStub stub, IndexSink sink) {
        for (String varName : stub.getVariableDefinitions()) {
            sink.occurrence(BashInjectionVarDefinitionsIndex.KEY, varName);
        }
    }
}
