package com.ansorgit.plugins.bash.lang.psi.stubs.index;

import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class BashInjectionVarDefinitionsIndex extends StringStubIndexExtension<BashLanguageInjectionHost> {
    public static final StubIndexKey<String, BashLanguageInjectionHost> KEY = StubIndexKey.createIndexKey("bash.injectionVarDefs");

    @NotNull
    @Override
    public StubIndexKey<String, BashLanguageInjectionHost> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return BashIndexVersion.STUB_INDEX_VERSION;
    }
}
