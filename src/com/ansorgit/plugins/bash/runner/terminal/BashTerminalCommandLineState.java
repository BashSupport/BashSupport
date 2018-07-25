/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.runner.terminal;

import com.ansorgit.plugins.bash.runner.BashRunConfigUtil;
import com.ansorgit.plugins.bash.runner.BashRunConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.openapi.actionSystem.AnAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class BashTerminalCommandLineState extends CommandLineState {
    private final BashRunConfiguration runConfig;

    BashTerminalCommandLineState(BashRunConfiguration runConfig, ExecutionEnvironment environment) {
        super(environment);
        this.runConfig = runConfig;
    }

    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        String workingDir = BashRunConfigUtil.findWorkingDir(runConfig);
        GeneralCommandLine cmd = BashRunConfigUtil.createCommandLine(workingDir, runConfig);

        BashLocalTerminalRunner myRunner = new BashLocalTerminalRunner(runConfig.getProject(), runConfig.getScriptName(), cmd);
        myRunner.run();
        return new BashTerminalExecutionResult(myRunner.getProcessHandler());
    }

    @Nullable
    @Override
    protected ConsoleView createConsole(@NotNull Executor executor) throws ExecutionException {
        return null;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        throw new UnsupportedOperationException("not supported by terminal implementation");
    }

    private static class BashTerminalExecutionResult implements ExecutionResult {
        private final ProcessHandler processHandler;

        public BashTerminalExecutionResult(ProcessHandler processHandler) {
            this.processHandler = processHandler;
        }

        @Override
        public ExecutionConsole getExecutionConsole() {
            return null;
        }

        @Override
        public AnAction[] getActions() {
            return new AnAction[0];
        }

        @Override
        public ProcessHandler getProcessHandler() {
            return processHandler;
        }
    }
}