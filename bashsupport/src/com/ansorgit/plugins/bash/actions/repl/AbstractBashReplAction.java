/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractBashReplAction.java, Class: AbstractBashReplAction
 * Last modified: 2010-03-04
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

package com.ansorgit.plugins.bash.actions.repl;

import com.ansorgit.plugins.bash.repl.ReplManager;
import com.ansorgit.plugins.bash.repl.ReplPanel;
import com.ansorgit.plugins.bash.settings.facet.BashFacet;
import com.ansorgit.plugins.bash.settings.facet.BashFacetType;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractBashReplAction extends AnAction {

    static Module getModule(AnActionEvent e) {
        Module module = e.getData(DataKeys.MODULE);
        if (module == null) {
            final Project project = e.getData(DataKeys.PROJECT);
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
                if (module == null) {
                    module = modules[0];
                }
            }
        }
        return module;
    }

    static void evaluateInCurrentRepl(@NotNull String textToEvaluate, @NotNull AnActionEvent event) {
        evaluateInCurrentRepl(textToEvaluate, event, true, false);
    }

    static void evaluateInCurrentRepl(@NotNull String textToEvaluate, @NotNull AnActionEvent event, boolean showErrors, boolean requestFocus) {
        Project project = event.getData(DataKeys.PROJECT);
        if (project == null) {
            return;
        }

        ReplPanel repl = getCurrentRepl(event);
        if (repl == null) {
            if (showErrors) {
                Messages.showErrorDialog(project,
                        "evaluate.norepl.message",
                        "evaluate.norepl.title");
            }

            return;
        }

        /*if (ClojurePsiFactory.getInstance(project).hasSyntacticalErrors(textToEvaluate)) {
         if (showErrors) {
           Messages.showErrorDialog(project,
                 ClojureBundle.message("evaluate.incorrect.sexp"),
                 ClojureBundle.message("evaluate.incorrect.cannot.evaluate"));
         }
         return;
       } */

        //repl.writeToCurrentRepl(textToEvaluate, requestFocus);
    }


    @Nullable
    static ReplManager getReplManager(final AnActionEvent e) {
        final Project project = PlatformDataKeys.PROJECT.getData(e.getDataContext());
        return project == null ? null : ReplManager.getInstance(project);
    }

    @Nullable
    static ReplPanel getCurrentRepl(final AnActionEvent e) {
        final ReplManager replManager = getReplManager(e);
        if (replManager != null) {
            return replManager.getCurrentRepl();
        }

        return null;
    }

    //  protected @Nullable String getFilePath(@NotNull AnActionEvent e) {
    //    VirtualFile vfile = e.getData(DataKeys.VIRTUAL_FILE);
    //    return vfile != null ? vfile.getPath() : null;
    //  }

}
