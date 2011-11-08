/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashCommandLineState.java, Class: BashCommandLineState
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.util.text.StringUtil;

/**
 * This code bases on the intellij-batch plugin by wibotwi.
 *
 * @author wibotwi, jansorg
 */
public class BashCommandLineState extends CommandLineState {
    private final BashRunConfiguration runConfiguration;

    public BashCommandLineState(BashRunConfiguration runConfiguration, ExecutionEnvironment env) {
        super(env);
        this.runConfiguration = runConfiguration;
    }

    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        GeneralCommandLine commandLine = generateCommandLine();

        OSProcessHandler osProcessHandler = new OSProcessHandler(
                commandLine.createProcess(),
                commandLine.getCommandLineString());
        //osProcessHandler.putUserData(OSProcessHandler.SILENTLY_DESTROY_ON_CLOSE, Boolean.TRUE);

        //notifies in the status bar with a message and the exit code
        ProcessTerminatedListener.attach(osProcessHandler, runConfiguration.getProject());

        return osProcessHandler;
    }

    private GeneralCommandLine generateCommandLine() {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath(runConfiguration.getInterpreterPath());
        commandLine.getParametersList().addParametersString(runConfiguration.getInterpreterOptions());

        if (!StringUtil.isEmptyOrSpaces(runConfiguration.getScriptName())) {
            commandLine.addParameter(runConfiguration.getScriptName());
        }

        commandLine.getParametersList().addParametersString(runConfiguration.getScriptParameters());

        if (!StringUtil.isEmptyOrSpaces(runConfiguration.getWorkingDirectory())) {
            commandLine.setWorkDirectory(runConfiguration.getWorkingDirectory());
        }

        commandLine.setEnvParams(runConfiguration.getEnvs());
        commandLine.setPassParentEnvs(runConfiguration.isPassParentEnvs());

        return commandLine;
    }
}