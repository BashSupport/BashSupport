/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ModuleFileTreeTable.java, Class: ModuleFileTreeTable
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

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;

/**
 * User: jansorg
 * Date: Feb 12, 2010
 * Time: 10:35:52 PM
 */
class ModuleFileTreeTable extends AbstractFileTreeTable<FileMode> {
    public ModuleFileTreeTable(Module module, final Map<VirtualFile, FileMode> mapping) {
        super(module, FileMode.class, "Ignore / Accept", new ModuleFileFilter(module));
        reset(mapping);

        getValueColumn().setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(final JTable table, final Object value,
                                                           final boolean isSelected, final boolean hasFocus, final int row, final int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                final FileMode t = (FileMode) value;
                final Object userObject = table.getModel().getValueAt(row, 0);
                final VirtualFile file = userObject instanceof VirtualFile ? (VirtualFile) userObject : null;
                final Pair<String, Boolean> pair = ChangeFileTypeUpdateGroup.update(file);
                final boolean enabled = file == null || pair.getSecond();

                if (t != null) {
                    setText(t.getDisplayName());
                } else if (file != null) {
                    FileMode fileMode = mapping.get(file);
                    if (fileMode != null) {
                        setText(fileMode.getDisplayName());
                    }
                    //fixme
                }

                setEnabled(enabled);
                return this;
            }
        });

        getValueColumn().setCellEditor(new DefaultCellEditor(new JComboBox()) {
            private VirtualFile myVirtualFile;

            {
                delegate = new EditorDelegate() {
                    public void setValue(Object value) {
                        getTableModel().setValueAt(value, new DefaultMutableTreeNode(myVirtualFile), -1);
                    }

                    public Object getCellEditorValue() {
                        return getTableModel().getValueAt(new DefaultMutableTreeNode(myVirtualFile), 1);
                    }
                };
            }

            public Component getTableCellEditorComponent(JTable table, final Object value, boolean isSelected, int row, int column) {
                final Object o = table.getModel().getValueAt(row, 0);
                myVirtualFile = o instanceof Module ? null : (VirtualFile) o;

                final ChooseFileModeAction changeAction = new ChooseFileModeAction(myVirtualFile) {
                    protected void chosen(VirtualFile virtualFile, FileMode mode) {
                        getValueColumn().getCellEditor().stopCellEditing();
                        boolean clearSettings = clearSubdirectoriesOnDemandOrCancel(virtualFile, "There are settings specified for the subdirectories. Override them?", "Override Subdirectory Settings");
                        if (clearSettings) {
                            getTableModel().setValueAt(mode, new DefaultMutableTreeNode(virtualFile), 1);
                            mapping.put(virtualFile, mode);
                        }
                    }
                };

                Presentation templatePresentation = changeAction.getTemplatePresentation();
                final JComponent comboComponent = changeAction.createCustomComponent(templatePresentation);

                DataContext dataContext = SimpleDataContext.getSimpleContext(PlatformDataKeys.VIRTUAL_FILE.getName(),
                        myVirtualFile,
                        SimpleDataContext.getProjectContext(getModule().getProject()));

                AnActionEvent event = new AnActionEvent(null,
                        dataContext,
                        ActionPlaces.UNKNOWN,
                        templatePresentation, ActionManager.getInstance(), 0);
                changeAction.update(event);

                editorComponent = comboComponent;

                comboComponent.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentShown(final ComponentEvent e) {
                        press((Container) e.getComponent());
                    }
                });

                FileMode mode = (FileMode) getTableModel().getValueAt(new DefaultMutableTreeNode(myVirtualFile), 1);
                templatePresentation.setText(mode == null ? "" : mode.getDisplayName());
                comboComponent.revalidate();

                return editorComponent;
            }
        });
    }

    @Override
    protected boolean isNullObject(final FileMode value) {
        //return value == FileMode.auto();
        return value == FileMode.defaultMode();
    }

    @Override
    protected boolean isValueEditableForFile(final VirtualFile virtualFile) {
        //return ChangeEncodingUpdateGroup.update(virtualFile).getSecond();
        return true;
    }
}
