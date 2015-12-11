package com.ansorgit.plugins.bash.lang.psi.stubs.api;

import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.util.ArrayFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author jansorg
 */
public interface BashFunctionDefStub extends NamedStub<BashFunctionDef> {
    ArrayFactory<BashFunctionDef> ARRAY_FACTORY = new ArrayFactory<BashFunctionDef>() {
        @NotNull
        @Override
        public BashFunctionDef[] create(int count) {
            return new BashFunctionDef[count];
        }
    };
}
