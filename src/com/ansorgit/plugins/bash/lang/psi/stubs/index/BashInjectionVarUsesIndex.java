package com.ansorgit.plugins.bash.lang.psi.stubs.index;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class BashInjectionVarUsesIndex extends StringStubIndexExtension<BashCommand> {
    public static final StubIndexKey<String, BashCommand> KEY = StubIndexKey.createIndexKey("bash.injectionVarUses");

    @NotNull
    @Override
    public StubIndexKey<String, BashCommand> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return BashIndexVersion.STUB_INDEX_VERSION;
    }
}
