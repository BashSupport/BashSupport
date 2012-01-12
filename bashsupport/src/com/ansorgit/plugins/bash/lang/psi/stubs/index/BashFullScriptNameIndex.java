package com.ansorgit.plugins.bash.lang.psi.stubs.index;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;

/**
 * User: jansorg
 * Date: 11.01.12
 * Time: 22:46
 */
public class BashFullScriptNameIndex extends StringStubIndexExtension<BashFile> {
    public static final StubIndexKey<String, BashFile> KEY = StubIndexKey.createIndexKey("bash.script.fullname");

    @Override
    public StubIndexKey<String, BashFile> getKey() {
        return KEY;
    }
}
