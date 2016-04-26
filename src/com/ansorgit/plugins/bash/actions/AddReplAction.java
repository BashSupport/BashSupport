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

package com.ansorgit.plugins.bash.actions;

import com.ansorgit.plugins.bash.runner.repl.BashConsoleRunner;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public class AddReplAction extends AnAction {
    private static final Logger log = Logger.getInstance("AddReplAction");
    private BashConsoleRunner consoleRunner;

    public AddReplAction() {
        getTemplatePresentation().setIcon(BashIcons.BASH_FILE_ICON);
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        Presentation presentation = e.getPresentation();
        presentation.setEnabled(getModule(e) != null);
    }

    @TestOnly
    BashConsoleRunner getConsoleRunner() {
        return consoleRunner;
    }

    public void actionPerformed(AnActionEvent e) {
        Module module = getModule(e);

        if (module != null) {
            try {
                Project project = module.getProject();
                VirtualFile baseDir = project.getBaseDir();

                if (baseDir != null) {
                    consoleRunner = new BashConsoleRunner(project, baseDir.getPath());
                    consoleRunner.initAndRun();
                }
            } catch (com.intellij.execution.ExecutionException ex) {
                log.warn("Error running bash repl", ex);
            }
        }
    }

    @Nullable
    private static Module getModule(AnActionEvent e) {
        Module module = e.getData(LangDataKeys.MODULE);
        if (module == null) {
            Project project = e.getData(LangDataKeys.PROJECT);
            if (project == null) {
                return null;
            }

            final Module[] modules = ModuleManager.getInstance(project).getModules();
            if (modules.length == 1) {
                module = modules[0];
            }
        }

        return module;
    }
}
