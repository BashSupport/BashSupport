package com.ansorgit.plugins.bash.lang.psi.stubs.index;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class BashVarIndex extends StringStubIndexExtension<BashVarDef> {
    public static final StubIndexKey<String, BashVarDef> KEY = StubIndexKey.createIndexKey("bash.var");

    @NotNull
    @Override
    public StubIndexKey<String, BashVarDef> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return BashIndexVersion.STUB_INDEX_VERSION;
    }

}
