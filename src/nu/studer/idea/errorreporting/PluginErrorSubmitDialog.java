/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: PluginErrorSubmitDialog.java, Class: PluginErrorSubmitDialog
 * Last modified: 2010-06-03
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
package nu.studer.idea.errorreporting;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.util.net.HTTPProxySettingsDialog;
import org.jdom.Document;
import org.jdom.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class defines the error submission dialog which allows the user to enter error details. It looks very similar to the built-in IDEA error
 * submission dialog.
 *
 * @author <a href="mailto:intellij@studer.nu">Etienne Studer</a>, Jul 14, 2006
 */
public class PluginErrorSubmitDialog extends DialogWrapper {
    private static final Logger LOGGER = Logger.getInstance(PluginErrorSubmitDialog.class.getName());

    @SuppressWarnings({"AnalyzingVariableNaming"})
    public String USERNAME;// persisted setting

    private JTextArea descriptionTextArea;
    private JTextField userTextField;
    private JPanel contentPane;
    private AbstractAction proxyAction;

    protected PluginErrorSubmitDialog(Component inParent) {
        super(inParent, true);

        setTitle(PluginErrorReportSubmitterBundle.message("submission.dialog.title"));

        descriptionTextArea = new JTextArea(10, 50);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        userTextField = new JTextField();

        JPanel descriptionPane = new JPanel(new BorderLayout());
        descriptionPane.add(new JLabel(PluginErrorReportSubmitterBundle.message("submission.dialog.label.description")), BorderLayout.NORTH);
        descriptionPane.add(descriptionTextArea, BorderLayout.CENTER);

        JPanel userPane = new JPanel(new BorderLayout());
        userPane.add(new JLabel(PluginErrorReportSubmitterBundle.message("submission.dialog.label.user")), BorderLayout.NORTH);
        userPane.add(userTextField, BorderLayout.CENTER);

        contentPane = new JPanel(new BorderLayout(0, 10));
        contentPane.add(descriptionPane, BorderLayout.CENTER);
        contentPane.add(userPane, BorderLayout.SOUTH);

        setOKButtonText(PluginErrorReportSubmitterBundle.message("submission.dialog.button.send"));

        proxyAction = new AbstractAction(PluginErrorReportSubmitterBundle.message("submission.dialog.button.proxy")) {
            public void actionPerformed(ActionEvent inActionEvent) {
                HTTPProxySettingsDialog settingsDialog = new HTTPProxySettingsDialog();
                settingsDialog.show();
            }
        };

        init();
    }

    public void prepare() {
        File file = new File(getOptionsFilePath());
        if (file.exists()) {
            try {
                Document document = JDOMUtil.loadDocument(file);
                Element applicationElement = document.getRootElement();
                if (applicationElement == null) {
                    throw new InvalidDataException("Expected root element >application< not found");
                }
                Element componentElement = applicationElement.getChild("component");
                if (componentElement == null) {
                    throw new InvalidDataException("Expected element >component< not found");
                }

                DefaultJDOMExternalizer.readExternal(this, componentElement);
                userTextField.setText(USERNAME);
            } catch (Exception e) {
                LOGGER.info("Unable to read configuration file", e);
            }
        }
    }

    public void persist() {
        try {
            Element applicationElement = new Element("application");
            Element componentElement = new Element("component");
            applicationElement.addContent(componentElement);

            USERNAME = userTextField.getText();
            DefaultJDOMExternalizer.writeExternal(this, componentElement);

            Document document = new Document(applicationElement);
            JDOMUtil.writeDocument(document, getOptionsFilePath(), "\r\n");
        } catch (Exception e) {
            LOGGER.info("Unable to persist configuration file", e);
        }
    }

    private String getOptionsFilePath() {
        String optionsPath = PathManager.getOptionsPath();
        String filePath = optionsPath + File.separator + "pluginErrorReportSubmitter.xml";
        return filePath;
    }

    protected JComponent createCenterPanel() {
        return contentPane;
    }

    protected Action[] createLeftSideActions() {
        return new Action[]{proxyAction};
    }

    public JComponent getPreferredFocusedComponent() {
        return descriptionTextArea;
    }

    public String getDescription() {
        return descriptionTextArea.getText();
    }

    public String getUser() {
        return userTextField.getText();
    }
}
