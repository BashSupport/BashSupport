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

import com.intellij.execution.ui.CommonProgramParametersPanel;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.MacroAwareTextBrowseFolderListener;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;

public class BashConfigForm extends CommonProgramParametersPanel {
    private LabeledComponent<RawCommandLineEditor> interpreterParametersComponent;
    private LabeledComponent<JComponent> interpreterPathComponent;
    private TextFieldWithBrowseButton interpreterPathField;

    private JBCheckBox projectInterpreterCheckbox;
    private LabeledComponent<JBCheckBox> projectInterpreterLabeled;

    private LabeledComponent<JComponent> scriptNameComponent;
    private TextFieldWithBrowseButton scriptNameField;

    @Override
    public void setAnchor(JComponent anchor) {
        super.setAnchor(anchor);
        projectInterpreterCheckbox.setAnchor(anchor);
        interpreterParametersComponent.setAnchor(anchor);
        interpreterPathComponent.setAnchor(anchor);
        scriptNameComponent.setAnchor(anchor);
    }

    @Override
    protected void setupAnchor() {
        super.setupAnchor();
        myAnchor = UIUtil.mergeComponentsWithAnchor(this, projectInterpreterLabeled, interpreterParametersComponent, interpreterPathComponent, scriptNameComponent);
    }

    protected void initOwnComponents() {
        Project project = getProject();

        FileChooserDescriptor chooseInterpreterDescriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
        chooseInterpreterDescriptor.setTitle("Choose Interpreter...");

        interpreterPathField = new TextFieldWithBrowseButton();
        interpreterPathField.addBrowseFolderListener(new MacroAwareTextBrowseFolderListener(chooseInterpreterDescriptor, project));

        interpreterPathComponent = LabeledComponent.create(createComponentWithMacroBrowse(interpreterPathField), "Interpreter path:");
        interpreterPathComponent.setLabelLocation(BorderLayout.WEST);

        projectInterpreterCheckbox = new JBCheckBox();
        projectInterpreterCheckbox.setToolTipText("If enabled then the interpreter path configured in the project settings will be used instead of a custom location.");
        projectInterpreterCheckbox.addChangeListener(e -> {
            boolean selected = projectInterpreterCheckbox.isSelected();
            UIUtil.setEnabled(interpreterPathComponent, !selected, true);
        });
        projectInterpreterLabeled = LabeledComponent.create(projectInterpreterCheckbox, "Use project interpreter");
        projectInterpreterLabeled.setLabelLocation(BorderLayout.WEST);

        interpreterParametersComponent = LabeledComponent.create(new RawCommandLineEditor(), "Interpreter options");
        interpreterParametersComponent.setLabelLocation(BorderLayout.WEST);

        scriptNameField = new TextFieldWithBrowseButton();
        scriptNameField.addBrowseFolderListener(new MacroAwareTextBrowseFolderListener(FileChooserDescriptorFactory.createSingleLocalFileDescriptor(), project));

        scriptNameComponent = LabeledComponent.create(createComponentWithMacroBrowse(scriptNameField), "Script:");
        scriptNameComponent.setLabelLocation(BorderLayout.WEST);
    }

    @Override
    protected void addComponents() {
        initOwnComponents();

        add(scriptNameComponent);
        add(projectInterpreterLabeled);
        add(interpreterPathComponent);
        add(interpreterParametersComponent);

        super.addComponents();
    }

    public void resetFormTo(BashRunConfiguration configuration) {
        projectInterpreterCheckbox.setSelected(configuration.isUseProjectInterpreter());
        interpreterPathField.setText(configuration.getInterpreterPath());
        interpreterParametersComponent.getComponent().setText(configuration.getInterpreterOptions());
        scriptNameField.setText(configuration.getScriptName());
    }

    public void applySettingsTo(BashRunConfiguration configuration) {
        configuration.setUseProjectInterpreter(projectInterpreterCheckbox.isSelected());
        configuration.setInterpreterPath(interpreterPathField.getText());
        configuration.setInterpreterOptions(interpreterParametersComponent.getComponent().getText());
        configuration.setScriptName(scriptNameField.getText());
    }
}
