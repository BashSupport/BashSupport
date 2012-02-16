/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: AddReplAction.java, Class: AddReplAction
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

package com.ansorgit.plugins.bash.actions;

import com.ansorgit.plugins.bash.runner.repl.BashConsoleRunner;
import com.ansorgit.plugins.bash.settings.facet.BashFacet;
import com.ansorgit.plugins.bash.settings.facet.BashFacetType;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

public class AddReplAction extends AnAction {
    private static final com.intellij.openapi.diagnostic.Logger log = com.intellij.openapi.diagnostic.Logger.getInstance("AddReplAction");

    public AddReplAction() {
        getTemplatePresentation().setIcon(BashIcons.BASH_FILE_ICON);
    }

    @Override
    public void update(AnActionEvent e) {
        final Module m = getModule(e);

        final Presentation presentation = e.getPresentation();
        if (m == null) {
            presentation.setEnabled(false);
            return;
        }

        presentation.setEnabled(true);

        super.update(e);
    }

    public void actionPerformed(AnActionEvent e) {
        Module module = getModule(e);

        if (module != null) {
            try {
                Project project = module.getProject();
                VirtualFile baseDir = project.getBaseDir();
                if (baseDir != null) {
                    BashConsoleRunner consoleRunner = new BashConsoleRunner(project, baseDir.getPath());
                    consoleRunner.initAndRun();
                }
            } catch (com.intellij.execution.ExecutionException ex) {
                log.warn("Error running bash repl", ex);
            }
        }
    }

    @Nullable
    static Module getModule(AnActionEvent e) {
        Module module = e.getData(LangDataKeys.MODULE);
        if (module == null) {
            final Project project = e.getData(LangDataKeys.PROJECT);
            if (project == null) {
                return null;
            }
            final Module[] modules = ModuleManager.getInstance(project).getModules();
            if (modules.length == 1) {
                module = modules[0];
            } else {
                for (Module m : modules) {
                    final FacetManager manager = FacetManager.getInstance(m);
                    final BashFacet clFacet = manager.getFacetByType(BashFacetType.INSTANCE.getId());
                    if (clFacet != null) {
                        module = m;
                        break;
                    }
                }

                if (module == null && modules.length > 0) {
                    module = modules[0];
                }
            }
        }

        return module;
    }
}
