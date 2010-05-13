/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractBashReplAction.java, Class: AbstractBashReplAction
 * Last modified: 2010-05-13
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

import com.ansorgit.plugins.bash.settings.facet.BashFacet;
import com.ansorgit.plugins.bash.settings.facet.BashFacetType;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;

abstract class AbstractBashReplAction extends AnAction {

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
                if (module == null) {
                    module = modules[0];
                }
            }
        }
        return module;
    }
}
