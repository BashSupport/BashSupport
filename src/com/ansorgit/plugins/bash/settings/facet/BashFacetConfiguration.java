/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFacetConfiguration.java, Class: BashFacetConfiguration
 * Last modified: 2010-02-17
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

package com.ansorgit.plugins.bash.settings.facet;

import com.ansorgit.plugins.bash.settings.facet.ui.BashFacetUI;
import com.ansorgit.plugins.bash.settings.facet.ui.FileMode;
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.containers.ContainerUtil;
import org.jdom.Element;

import java.io.Serializable;
import java.util.*;

@State(
        name = "BashSupportFacet",
        storages = {
                @Storage(
                        id = "BashSupportFacetSettings",
                        file = "$MODULE_FILE$"
                )
        }
)

public class BashFacetConfiguration implements FacetConfiguration, Serializable {
    private OperationMode operationMode = OperationMode.IgnoreAll;
    private Logger LOG = Logger.getInstance("BashFacetConfig");

    public BashFacetConfiguration() {
    }

    public Element getState() {
        Element element = new Element("bashsupport");
        try {
            this.writeExternal(element);
        }
        catch (WriteExternalException e) {
            LOG.info(e);
        }
        return element;
    }

    public void loadState(Element state) {
        try {
            this.readExternal(state);
        }
        catch (InvalidDataException e) {
            LOG.info(e);
        }
    }

    //fixme thread safety ?
    private Map<VirtualFile, FileMode> mapping = new HashMap<VirtualFile, FileMode>();

    public FileMode findMode(VirtualFile file) {
        if (operationMode == OperationMode.AcceptAll) {
            return FileMode.accept();
        }

        if (operationMode == OperationMode.IgnoreAll) {
            return FileMode.ignore();
        }

        //custom mode
        if (mapping.containsKey(file)) {
            return mapping.get(file);
        }

        VirtualFile parent = file.getParent();
        if (parent != null) {
            return findMode(parent);
        }

        return mapping.containsKey(null) ? mapping.get(null) : FileMode.defaultMode();
    }

    public static enum OperationMode {
        IgnoreAll, AcceptAll, Custom
    }

    //private Element settings;

    public FacetEditorTab[] createEditorTabs(FacetEditorContext facetEditorContext, FacetValidatorsManager facetValidatorsManager) {
        return new FacetEditorTab[]{
                new BashFacetUI(this, facetEditorContext, facetValidatorsManager)
        };
    }

    @Deprecated
    public void readExternal(Element element) throws InvalidDataException {
        Element modeChild = element.getChild("operationMode");
        if (modeChild != null) {
            String modeString = modeChild.getAttributeValue("type", OperationMode.IgnoreAll.name());
            operationMode = OperationMode.valueOf(modeString);
        }

        List<Element> files = element.getChildren("file");
        for (Element fileElement : files) {
            String url = fileElement.getAttributeValue("url");

            String modeId = fileElement.getAttributeValue("mode");
            if (modeId == null) {
                continue;
            }

            FileMode mode = FileMode.forId(modeId);
            if (mode == null) {
                continue;
            }

            VirtualFile file = url.equals("MODULE") ? null : VirtualFileManager.getInstance().findFileByUrl(url);
            if (file != null || url.equals("MODULE")) {
                mapping.put(file, mode);
            }
        }

        //operationMode = OperationMode.valueOf(element.getAttributeValue("operationMode"));        
    }

    @Deprecated
    public void writeExternal(Element element) throws WriteExternalException {
        Element modeElement = new Element("operationMode");
        modeElement.setAttribute("type", operationMode.name());
        element.addContent(modeElement);

        List<VirtualFile> files = new ArrayList<VirtualFile>(mapping.keySet());
        ContainerUtil.quickSort(files, new Comparator<VirtualFile>() {
            public int compare(final VirtualFile o1, final VirtualFile o2) {
                if (o1 == null || o2 == null) return o1 == null ? o2 == null ? 0 : 1 : -1;
                return o1.getPath().compareTo(o2.getPath());
            }
        });

        for (VirtualFile file : files) {
            FileMode mode = mapping.get(file);
            Element child = new Element("file");
            element.addContent(child);

            child.setAttribute("url", file == null ? "MODULE" : file.getUrl());
            child.setAttribute("mode", mode.getId());
        }
    }

    public OperationMode getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(OperationMode operationMode) {
        this.operationMode = operationMode;
    }

    public Map<VirtualFile, FileMode> getMapping() {
        return mapping;
    }

    public void setMapping(Map<VirtualFile, FileMode> newMapping) {
        mapping.clear();
        mapping.putAll(newMapping);
    }
}