package com.ansorgit.plugins.bash.lang.psi.stubs.api;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.intellij.psi.stubs.StubElement;

/**
 * @author jansorg
 */
public interface BashIncludeCommandStub extends BashCommandStubBase<BashIncludeCommand> {
    String getIncludedFilename();

    String getIncluderFilename();
}
