package com.ansorgit.plugins.bash.lang.psi.stubs.index;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;

/**
 * User: jansorg
 * Date: 11.01.12
 * Time: 22:46
 */
public class BashVarDefIndex extends StringStubIndexExtension<BashVarDef> {
    public static final StubIndexKey<String, BashVarDef> KEY = StubIndexKey.createIndexKey("bash.vardef");

    @Override
    public StubIndexKey<String, BashVarDef> getKey() {
        return KEY;
    }

    @Override
    public int getVersion() {
        return BashIndexVersion.VERSION;
    }
}
