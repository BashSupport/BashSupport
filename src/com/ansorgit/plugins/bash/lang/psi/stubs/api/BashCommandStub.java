package com.ansorgit.plugins.bash.lang.psi.stubs.api;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.intellij.psi.stubs.StubElement;

/**
 * @author jansorg
 */
public interface BashCommandStub extends StubElement<BashCommand> {
    String getBashCommandFilename();
}
