/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFacetUI.java, Class: BashFacetUI
 * Last modified: 2010-02-16
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

package com.ansorgit.plugins.bash.settings.facet.ui;

import com.ansorgit.plugins.bash.settings.facet.BashFacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * GUI settings which are displayed for a Bash module facet.
 * <p/>
 * User: jansorg
 * Date: Feb 11, 2010
 * Time: 10:21:10 PM
 */
public class BashFacetUI extends FacetEditorTab {
    private JRadioButton ignoreFilesWithoutExtensionRadioButton;
    private JRadioButton acceptAllFilesWithoutRadioButton;
    private JRadioButton customSettingsRadioButton;
    private JPanel basePanel;
    private JScrollPane treeScollArea;

    private ModuleFileTreeTable fileTreeTable;

    private final BashFacetConfiguration facetConfiguration;
    private final FacetEditorContext facetEditorContext;

    public BashFacetUI(BashFacetConfiguration facetConfiguration, FacetEditorContext facetEditorContext, FacetValidatorsManager facetValidatorsManager) {
        this.facetConfiguration = facetConfiguration;
        this.facetEditorContext = facetEditorContext;
    }

    @Nls
    public String getDisplayName() {
        return "BashSupport";
    }

    private BashFacetConfiguration.OperationMode findMode() {
        if (ignoreFilesWithoutExtensionRadioButton.isSelected()) {
            return BashFacetConfiguration.OperationMode.IgnoreAll;
        }

        if (acceptAllFilesWithoutRadioButton.isSelected()) {
            return BashFacetConfiguration.OperationMode.AcceptAll;
        }

        return BashFacetConfiguration.OperationMode.Custom;
    }

    private void setMode(BashFacetConfiguration.OperationMode mode) {
        acceptAllFilesWithoutRadioButton.setSelected(mode == BashFacetConfiguration.OperationMode.AcceptAll);
        ignoreFilesWithoutExtensionRadioButton.setSelected(mode == BashFacetConfiguration.OperationMode.IgnoreAll);
        customSettingsRadioButton.setSelected(mode == BashFacetConfiguration.OperationMode.Custom);

        basePanel.setEnabled(customSettingsRadioButton.isSelected());
        fileTreeTable.setEnabled(customSettingsRadioButton.isSelected());
    }

    public JComponent createComponent() {
        customSettingsRadioButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JRadioButton button = (JRadioButton) e.getSource();
                treeScollArea.setEnabled(button.isSelected());

                basePanel.setEnabled(customSettingsRadioButton.isSelected());
                fileTreeTable.setEnabled(customSettingsRadioButton.isSelected());
            }
        });

        fileTreeTable = new ModuleFileTreeTable(facetEditorContext.getModule(), facetConfiguration.getMapping());
        treeScollArea.setViewportView(fileTreeTable);

        reset();

        return basePanel;
    }

    public boolean isModified() {
        return findMode() != facetConfiguration.getOperationMode();
    }

    public void apply() throws ConfigurationException {
        facetConfiguration.setOperationMode(findMode());
    }

    public void reset() {
        setMode(facetConfiguration.getOperationMode());
    }

    public void disposeUIResources() {
    }

    private void createUIComponents() {
    }
}
