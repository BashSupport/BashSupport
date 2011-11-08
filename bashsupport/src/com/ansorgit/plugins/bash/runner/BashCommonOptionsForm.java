/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashCommonOptionsForm.java, Class: BashCommonOptionsForm
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

import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;

import javax.swing.*;
import java.util.Map;

/**
 * User: jansorg
 * Date: 10.07.2009
 * Time: 21:43:12
 */
public class BashCommonOptionsForm implements CommonBashRunConfigurationParams {
    private JPanel rootPanel;
    private RawCommandLineEditor interpreterOptions;
    private EnvironmentVariablesComponent environmentVariablesEdit;
    private TextFieldWithBrowseButton bashInterpreterEdit;
    private TextFieldWithBrowseButton workingDirEdit;
    private BashRunConfiguration bashRunConfiguration;

    public BashCommonOptionsForm(BashRunConfiguration bashRunConfiguration) {
        this.bashRunConfiguration = bashRunConfiguration;
        bashInterpreterEdit.addBrowseFolderListener("Select Bash Interpreter", "", bashRunConfiguration.getProject(), BrowseFilesListener.SINGLE_FILE_DESCRIPTOR);
        workingDirEdit.addBrowseFolderListener("Select Working Directory", "", bashRunConfiguration.getProject(), BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR);
    }

    public String getInterpreterOptions() {
        return interpreterOptions.getText();
    }

    public void setInterpreterOptions(String options) {
        interpreterOptions.setText(options);
    }

    public String getWorkingDirectory() {
        return workingDirEdit.getText();
    }

    public void setWorkingDirectory(String workingDirectory) {
        workingDirEdit.setText(workingDirectory);
    }

    public Map<String, String> getEnvs() {
        return environmentVariablesEdit.getEnvs();
    }

    public void setEnvs(Map<String, String> envs) {
        environmentVariablesEdit.setEnvs(envs);
    }

    public String getInterpreterPath() {
        return bashInterpreterEdit.getText();
    }

    public void setInterpreterPath(String path) {
        this.bashInterpreterEdit.setText(path);
    }

    public JComponent getRootPanel() {
        return rootPanel;
    }
}
