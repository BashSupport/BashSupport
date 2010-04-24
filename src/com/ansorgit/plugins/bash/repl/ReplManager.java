/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ReplManager.java, Class: ReplManager
 * Last modified: 2010-04-24
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

package com.ansorgit.plugins.bash.repl;

import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages REPLs in a dedicated ToolWindow and the ToolWindow itself.
 *
 * @author spleaner
 */
public class ReplManager extends ContentManagerAdapter implements Disposable {
    public static final String REPL_TOOLWINDOW_ID = "repl.toolWindowName";

    private Project myProject;
    private ToolWindowManager myToolWindowManager;
    private ToolWindow myToolWindow;

    private List<ReplPanel> myRepls = new ArrayList<ReplPanel>();

    public ReplManager(@NotNull final Project project, @NotNull final ToolWindowManager toolWindowManager) {
        myProject = project;
        myToolWindowManager = toolWindowManager;

        Disposer.register(project, this);
    }

    public static ReplManager getInstance(@NotNull final Project project) {
        return ServiceManager.getService(project, ReplManager.class);
    }

    public void dispose() {
        myProject = null;
        myToolWindowManager = null;
    }

    public ReplPanel createNewRepl(@NotNull final Module module) {
        try {
            final ReplPanel replPanel = new ReplPanel(myProject, module);

            myRepls.add(replPanel);
            Disposer.register(this, replPanel);

            final ToolWindow toolWindow = getToolWindow();
            final ContentManager contentManager = toolWindow.getContentManager();
            final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

            final Content c = contentFactory.createContent(replPanel, getReplName(module), true);
            contentManager.addContent(c);
            contentManager.setSelectedContent(c);
            toolWindow.activate(null);
            return replPanel;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ConfigurationException e) {
            JOptionPane.showMessageDialog(null,
                    "Not configured",
                    "config.error.replNotConfiguredTitle",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "config.error.replNotConfiguredMessage",
                    "config.error.replNotConfiguredTitle",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    public void removeCurrentRepl() {
        if (myToolWindow != null) {
            final ContentManager contentManager = myToolWindow.getContentManager();
            final Content selected = contentManager.getSelectedContent();
            if (selected != null) {
                contentManager.removeContent(selected, true);
            }
        }
    }

    public void contentRemoved(final ContentManagerEvent event) {
        if (myToolWindow != null) {
            final ContentManager contentManager = myToolWindow.getContentManager();

            final Content content = event.getContent();
            final JComponent c = content.getComponent();
            if (c instanceof ReplPanel && myRepls.contains(c)) {
                final ReplPanel panel = myRepls.remove(myRepls.indexOf(c));
                Disposer.dispose(panel);
            }

            if (contentManager.getContentCount() == 0) {
                myToolWindowManager.unregisterToolWindow(REPL_TOOLWINDOW_ID);
                contentManager.removeContentManagerListener(this);
                myToolWindow = null;
            }
        }
    }

    private String getReplName(@NotNull final Module module) {
        return "BashSupport" + "-" + module.getName() + (myRepls.size() < 2 ? "" : ("-" + myRepls.size()));
    }

    public void closeReplToolWindow() {
        // TODO
    }

    public ToolWindow getToolWindow() {
        if (myToolWindow == null) {
            myToolWindow = myToolWindowManager.registerToolWindow(REPL_TOOLWINDOW_ID, true, ToolWindowAnchor.BOTTOM, true);
            myToolWindow.getContentManager().addContentManagerListener(this);
            myToolWindow.setIcon(BashIcons.BASH_FILE_ICON);
        }

        return myToolWindow;
    }

    public void init(@NotNull final Module module) {
        if (myRepls.size() == 0) {
            createNewRepl(module);
        }
    }

    public void activate() {
        if (myRepls.size() > 0) {
            getToolWindow().activate(null);
        }
    }

    public void renameCurrentRepl() {
        final ReplPanel repl = getCurrentRepl();
        if (repl != null) {
            final ContentManager contentManager = myToolWindow.getContentManager();
            final Content content = contentManager.getContent(repl);
            if (content != null) {
                final String oldName = content.getTabName();
                final String newTitle = (String) JOptionPane.showInputDialog(repl, null, "Rename",
                        JOptionPane.PLAIN_MESSAGE, null, null, oldName);

                if (newTitle != null) {
                    content.setDisplayName(newTitle);
                }
            }
        }
    }

    @Nullable
    public ReplPanel getCurrentRepl() {
        if (myToolWindow != null) {
            final Content content = myToolWindow.getContentManager().getSelectedContent();
            if (content != null) {
                if (content.getComponent() instanceof ReplPanel) {
                    return (ReplPanel) content.getComponent();
                }
            }
        }

        return null;
    }

    public boolean hasActiveRepls() {
        return myRepls.size() > 0;
    }
}
