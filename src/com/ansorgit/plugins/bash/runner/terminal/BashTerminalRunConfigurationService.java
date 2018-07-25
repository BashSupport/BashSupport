package com.ansorgit.plugins.bash.runner.terminal;

import com.ansorgit.plugins.bash.runner.BashLineErrorFilter;
import com.ansorgit.plugins.bash.runner.BashRunConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;

/**
 * Service which is only enabled when the Terminal plugin is available.
 *
 * @author jansorg
 */
public class BashTerminalRunConfigurationService {
    public BashTerminalRunConfigurationService() {
        super();
    }

    public RunProfileState getState(BashRunConfiguration bashRunConfiguration, @NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        return new BashTerminalCommandLineState(bashRunConfiguration, env);
    }
}
