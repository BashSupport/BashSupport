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
package nu.studer.idea.errorreporting;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMUtil;
import org.jdom.Document;
import org.jdom.Element;

import javax.swing.*;
import java.awt.*;
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

    //private AbstractAction proxyAction;
    private final PluginErrorReportComponent reportComponent;

    protected PluginErrorSubmitDialog(Component inParent) {
        super(inParent, true);

        setTitle("BashSupport Error Submission");

        reportComponent = new PluginErrorReportComponent();

        setOKButtonText("Send Error Report");

        /*proxyAction = new AbstractAction(PluginErrorReportSubmitterBundle.message("submission.dialog.button.proxy")) {
            public void actionPerformed(ActionEvent inActionEvent) {
                HTTPProxySettingsDialog settingsDialog = new HTTPProxySettingsDialog();
                settingsDialog.show();
            }
        };*/

        init();
    }

    public void prepare(String additionalInfo, String stacktrace, String versionId) {
        reportComponent.descriptionField.setText(additionalInfo);
        reportComponent.stacktraceField.setText(stacktrace);
        reportComponent.versionField.setText(versionId);

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
                reportComponent.nameField.setText(USERNAME);
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

            USERNAME = reportComponent.nameField.getText();
            DefaultJDOMExternalizer.writeExternal(this, componentElement);

            Document document = new Document(applicationElement);
            JDOMUtil.writeDocument(document, getOptionsFilePath(), "\r\n");
        } catch (Exception e) {
            LOGGER.info("Unable to persist configuration file", e);
        }
    }

    private String getOptionsFilePath() {
        String optionsPath = PathManager.getOptionsPath();
        return optionsPath + File.separator + "pluginErrorReportSubmitter.xml";
    }

    protected JComponent createCenterPanel() {
        return reportComponent.contentPane;
    }

    protected Action[] createLeftSideActions() {
        //return new Action[]{proxyAction};
        return new Action[0];
    }

    public JComponent getPreferredFocusedComponent() {
        return reportComponent.descriptionField;
    }

    public String getDescription() {
        return reportComponent.descriptionField.getText();
    }

    public String getStackTrace() {
        return reportComponent.stacktraceField.getText();
    }

    public String getUser() {
        return reportComponent.nameField.getText();
    }
}
