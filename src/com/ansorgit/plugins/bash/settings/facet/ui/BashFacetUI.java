/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFacetUI.java, Class: BashFacetUI
 * Last modified: 2010-02-12
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
import com.ansorgit.plugins.bash.settings.facet.BashFacetSettings;
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

    private FileTreeTable fileTreeTable;

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

    private BashFacetSettings.Mode findMode() {
        if (ignoreFilesWithoutExtensionRadioButton.isSelected()) {
            return BashFacetSettings.Mode.IgnoreAll;
        } else if (acceptAllFilesWithoutRadioButton.isSelected()) {
            return BashFacetSettings.Mode.AcceptAll;
        } else {
            return BashFacetSettings.Mode.CustomSettings;
        }
    }

    public JComponent createComponent() {
        customSettingsRadioButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                treeScollArea.setEnabled(true);
            }
        });

        fileTreeTable = new FileTreeTable(facetEditorContext.getModule());
        treeScollArea.setViewportView(fileTreeTable);

        return basePanel;
    }

    public boolean isModified() {
        return findMode() != facetConfiguration.getState().mode;
    }

    public void apply() throws ConfigurationException {
        facetConfiguration.getState().mode = findMode();
    }

    public void reset() {
        //fixme
    }

    public void disposeUIResources() {
    }

    private void createUIComponents() {
    }
}
