package com.ansorgit.plugins.bash.lang.psi.stubs.index;

import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class BashFunctionNameIndex extends StringStubIndexExtension<BashFunctionDef> {
    public static final StubIndexKey<String, BashFunctionDef> KEY = StubIndexKey.createIndexKey("bash.function.name");

    @NotNull
    @Override
    public StubIndexKey<String, BashFunctionDef> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return BashIndexVersion.STUB_INDEX_VERSION;
    }
}
