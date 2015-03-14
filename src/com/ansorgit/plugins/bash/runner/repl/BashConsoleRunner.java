/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashConsoleRunner.java, Class: BashConsoleRunner
 * Last modified: 2010-05-29
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.CommandLineArgumentsProvider;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.project.Project;

import java.util.Collections;
import java.util.Map;

/**
 * ConsoleRunner implementation to run a bash shell in a window.
 * <p/>
 * User: jansorg
 * Date: 13.05.2010
 * Time: 00:45:23
 */
public class BashConsoleRunner extends AbstractConsoleRunnerWithHistory {
    private final String workingDir;

    public BashConsoleRunner(Project myProject, String workingDir) {
        super(myProject, "Bash", new CommandLineArgumentsProvider() {
            public String[] getArguments() {
                return new String[]{};
            }

            public boolean passParentEnvs() {
                return true;
            }

            public Map<String, String> getAdditionalEnvs() {
                return Collections.emptyMap();
            }
        }, workingDir);

        this.workingDir = workingDir;
    }

    @Override
    protected LanguageConsoleViewImpl createConsoleView() {
        LanguageConsoleViewImpl consoleView = new LanguageConsoleViewImpl(myProject, "Bash", BashFileType.BASH_LANGUAGE);
        consoleView.getConsole().getFile().putUserData(BashFile.LANGUAGE_CONSOLE_MARKER, true);

        return consoleView;
    }

    @Override
    protected Process createProcess() throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine();

        BashInterpreterDetection detect = new BashInterpreterDetection();
        //fixme make this configurable
        commandLine.setExePath(detect.findBestLocation());

        if (workingDir != null) {
            commandLine.setWorkDirectory(workingDir);
        }

        commandLine.addParameters(myProvider.getArguments());

        return commandLine.createProcess();
    }

    @Override
    protected OSProcessHandler createProcessHandler(Process process) {
        return new ColoredProcessHandler(process, getProviderCommandLine(myProvider));
    }
}
