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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.editor.inspections.quickfix.WordToDoublequotedStringQuickfix;
import com.ansorgit.plugins.bash.editor.inspections.quickfix.WordToSinglequotedStringQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Inspection which can wrap a word token inside of a string.
 * It offers options to either convert to a double quoted string or into
 * a single quoted string.
 *
 * @author jansorg
 */
public class WrapWordInStringInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder problemsHolder, boolean b) {
        return new BashVisitor() {
            @Override
            public void visitCombinedWord(BashWord word) {
                if (word.getParent() instanceof BashString) {
                    return;
                }

                if (word.isWrappable()) {
                    problemsHolder.registerProblem(word, "Unquoted string", new WordToDoublequotedStringQuickfix(word), new WordToSinglequotedStringQuickfix(word));
                }
            }
        };
    }
}
