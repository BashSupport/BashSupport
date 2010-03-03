/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: AddReplAction.java, Class: AddReplAction
 * Last modified: 2010-03-03
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
import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;

public class AddReplAction extends BashReplAction {
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
            ReplManager.getInstance(module.getProject()).createNewRepl(module);
        }
    }

}
