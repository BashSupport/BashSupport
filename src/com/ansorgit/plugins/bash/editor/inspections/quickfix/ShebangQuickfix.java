/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ShebangQuickfix.java, Class: ShebangQuickfix
 * Last modified: 2010-12-28 14:57
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

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Replaces an existing, but invalid shebang command with a known command.
 * <p/>
 * Date: 06.05.2009
 * Time: 13:52:33
 *
 * @author Joachim Ansorg
 */
public class ShebangQuickfix extends AbstractBashQuickfix {
    private final BashShebang shebang;
    private final String command;

    public ShebangQuickfix(BashShebang shebang, String command) {
        this.shebang = shebang;
        this.command = command;
    }

    @NotNull
    public String getName() {
        return "Replace with " + command;
    }

    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        shebang.updateCommand(command);
    }
}
