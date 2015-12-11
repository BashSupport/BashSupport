package com.ansorgit.plugins.bash.lang.psi.stubs.index;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class BashVarIndex extends StringStubIndexExtension<BashVar> {
    public static final StubIndexKey<String, BashVar> KEY = StubIndexKey.createIndexKey("bash.var");

    @NotNull
    @Override
    public StubIndexKey<String, BashVar> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return BashIndexVersion.STUB_INDEX_VERSION;
    }

}
