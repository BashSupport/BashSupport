package com.ansorgit.plugins.bash.runner;

import com.intellij.execution.ui.CommonProgramParametersPanel;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.MacroAwareTextBrowseFolderListener;
import com.intellij.ui.RawCommandLineEditor;

import javax.swing.*;
import java.awt.*;

public class BashConfigForm extends CommonProgramParametersPanel {
    private LabeledComponent<RawCommandLineEditor> interpreterParametersComponent;
    private LabeledComponent<JComponent> interpreterPathComponent;
    private TextFieldWithBrowseButton interpreterPathField;

    private LabeledComponent<JComponent> scriptNameComponent;
    private TextFieldWithBrowseButton scriptNameField;

    public BashConfigForm() {
    }

    protected void initOwnComponents() {
        interpreterParametersComponent = LabeledComponent.create(new RawCommandLineEditor(), "Interpreter options");
        interpreterParametersComponent.setLabelLocation(BorderLayout.WEST);

        FileChooserDescriptor chooseInterpreterDescriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
        chooseInterpreterDescriptor.setTitle("Choose interpreter...");

        interpreterPathField = new TextFieldWithBrowseButton();
        interpreterPathField.addBrowseFolderListener(new MacroAwareTextBrowseFolderListener(chooseInterpreterDescriptor, getProject()));
        interpreterPathComponent = LabeledComponent.create(createComponentWithMacroBrowse(interpreterPathField), "Interpreter path:");
        interpreterPathComponent.setLabelLocation(BorderLayout.WEST);


        FileChooserDescriptor chooseScriptDescriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
        scriptNameField = new TextFieldWithBrowseButton();
        scriptNameField.addBrowseFolderListener(new MacroAwareTextBrowseFolderListener(chooseScriptDescriptor, getProject()));

        scriptNameComponent = LabeledComponent.create(createComponentWithMacroBrowse(scriptNameField), "Script:");
        scriptNameComponent.setLabelLocation(BorderLayout.WEST);
    }


    @Override
    protected void addComponents() {
        initOwnComponents();

        add(scriptNameComponent);
        add(interpreterPathComponent);
        add(interpreterParametersComponent);

        super.addComponents();
    }

    public void resetBash(BashRunConfiguration configuration) {
        interpreterParametersComponent.getComponent().setText(configuration.getInterpreterOptions());
        interpreterPathField.setText(configuration.getInterpreterPath());
        scriptNameField.setText(configuration.getScriptName());
    }

    public void applyBashTo(BashRunConfiguration configuration) {
        configuration.setInterpreterOptions(interpreterParametersComponent.getComponent().getText());
        configuration.setInterpreterPath(interpreterPathField.getText());
        configuration.setScriptName(scriptNameField.getText());
    }
}
