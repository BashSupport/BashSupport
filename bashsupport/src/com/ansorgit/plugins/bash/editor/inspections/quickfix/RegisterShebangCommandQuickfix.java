/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: RegisterShebangCommandQuickfix.java, Class: RegisterShebangCommandQuickfix
 * Last modified: 2013-05-09
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

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.editor.inspections.inspections.FixShebangInspection;
import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class RegisterShebangCommandQuickfix extends AbstractBashQuickfix {
    private final FixShebangInspection inspection;
    private final BashShebang shebang;

    public RegisterShebangCommandQuickfix(FixShebangInspection fixShebangInspection, BashShebang shebang) {
        this.inspection = fixShebangInspection;
        this.shebang = shebang;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        inspection.registerShebangCommand(shebang.shellCommand(true));

        //trigger a change to remove this inspection
        shebang.updateCommand(shebang.shellCommand(false), null);
    }

    @NotNull
    @Override
    public String getName() {
        return "Mark as valid command";
    }
}
