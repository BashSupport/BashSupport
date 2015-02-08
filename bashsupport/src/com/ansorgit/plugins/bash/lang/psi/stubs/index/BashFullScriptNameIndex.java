package com.ansorgit.plugins.bash.lang.psi.stubs.index;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class BashFullScriptNameIndex extends StringStubIndexExtension<BashFile> {
    public static final StubIndexKey<String, BashFile> KEY = StubIndexKey.createIndexKey("bash.script.fullname");

    @NotNull
    @Override
    public StubIndexKey<String, BashFile> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return BashIndexVersion.STUB_INDEX_VERSION;
    }
}
