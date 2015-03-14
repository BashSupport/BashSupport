package com.ansorgit.plugins.bash.lang.psi.stubs.api;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.psi.stubs.StubElement;

/**
 * @author jansorg
 */
public interface BashIncludeCommandStub extends StubElement<BashIncludeCommand> {
    String getIncludedFilename();

    String getIncluderFilename();
}
