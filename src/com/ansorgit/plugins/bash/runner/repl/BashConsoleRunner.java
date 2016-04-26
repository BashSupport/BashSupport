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

package com.ansorgit.plugins.bash.runner.repl;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.util.BashInterpreterDetection;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.console.LanguageConsoleImpl;
import com.intellij.execution.console.LanguageConsoleView;
import com.intellij.execution.console.ProcessBackedConsoleExecuteActionHandler;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * ConsoleRunner implementation to run a bash shell in a window.
 */
public class BashConsoleRunner extends AbstractConsoleRunnerWithHistory<LanguageConsoleView> {
    public BashConsoleRunner(Project myProject, String workingDir) {
        super(myProject, "Bash", workingDir);
    }

    @Override
    protected LanguageConsoleView createConsoleView() {
        LanguageConsoleImpl consoleView = new LanguageConsoleImpl(getProject(), "Bash", BashFileType.BASH_LANGUAGE);
        consoleView.getFile().putUserData(BashFile.LANGUAGE_CONSOLE_MARKER, true);

        return consoleView;
    }

    @Override
    protected Process createProcess() throws ExecutionException {
        String bashLocation = BashInterpreterDetection.instance().findBestLocation();
        if (bashLocation == null) {
            throw new ExecutionException("Could not locate the bash executable");
        }

        GeneralCommandLine commandLine = new GeneralCommandLine().withWorkDirectory(getWorkingDir());
        commandLine.setExePath(bashLocation);
        return commandLine.createProcess();
    }

    @Override
    protected OSProcessHandler createProcessHandler(Process process) {
        return new ColoredProcessHandler(process, null);
    }

    @NotNull
    @Override
    protected ProcessBackedConsoleExecuteActionHandler createExecuteActionHandler() {
        return new ProcessBackedConsoleExecuteActionHandler(getProcessHandler(), true);
    }
}
