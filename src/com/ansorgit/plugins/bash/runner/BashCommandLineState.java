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

package com.ansorgit.plugins.bash.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;

class BashCommandLineState extends CommandLineState {
    private final BashRunConfiguration runConfig;

    BashCommandLineState(BashRunConfiguration runConfig, ExecutionEnvironment environment) {
        super(environment);
        this.runConfig = runConfig;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        String workingDir = BashRunConfigUtil.findWorkingDir(runConfig);
        GeneralCommandLine cmd = BashRunConfigUtil.createCommandLine(workingDir, runConfig);
        if (!cmd.getEnvironment().containsKey("TERM")) {
            cmd.getEnvironment().put("TERM", "xterm-256color");
        }

        OSProcessHandler processHandler = new KillableColoredProcessHandler(cmd);
        ProcessTerminatedListener.attach(processHandler, getEnvironment().getProject());

        //fixme handle path macros
        return processHandler;
    }
}