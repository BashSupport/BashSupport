/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashConsoleRunner.java, Class: BashConsoleRunner
 * Last modified: 2010-05-13
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

package com.ansorgit.plugins.bash.runner;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.util.BashInterpreterDetection;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.console.LanguageConsoleViewImpl;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.CommandLineArgumentsProvider;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.project.Project;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.Map;

/**
 * User: jansorg
 * Date: 13.05.2010
 * Time: 00:45:23
 */
public class BashConsoleRunner extends AbstractConsoleRunnerWithHistory {
    private final String workingDir;

    public BashConsoleRunner(Project myProject, String workingDir) {
        super(myProject, "Bash", new CommandLineArgumentsProvider() {
            public String[] getArguments() {
                return new String[]{
                        "--noediting", "-s"
                };
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
        consoleView.getConsole().getCurrentEditor().getComponent().addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    //run the action on alt+enter
                    BashConsoleRunner.this.runExecuteActionInner();
                }
            }

            public void keyPressed(KeyEvent e) {

            }

            public void keyReleased(KeyEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
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
