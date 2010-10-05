/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: WrapWordInStringInspection.java, Class: WrapWordInStringInspection
 * Last modified: 2010-03-24
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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.editor.inspections.quickfix.WordToDoublequotedStringQuickfix;
import com.ansorgit.plugins.bash.editor.inspections.quickfix.WordToSinglequotedStringQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Inspection which can wrap a word token inside of a string.
 * It offers options to either convert to a double quoted string or into
 * a single quoted string.
 * <p/>
 * User: jansorg
 * Date: 21.05.2009
 * Time: 10:32:31
 */
public class WrapWordInStringInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "ConvertToString";
    }

    @NotNull
    public String getShortName() {
        return "Convert to string";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Convert to a quoted or unquoted string";
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    @Override
    public String getStaticDescription() {
        return "This inspection can convert text which is not in a string into a string. For example \"echo a\" can be converted into \"echo 'a'\".";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder problemsHolder, boolean b) {
        return new BashVisitor() {
            @Override
            public void visitCombinedWord(BashWord word) {
                if (word.isWrappable()) {
                    problemsHolder.registerProblem(word, getShortName(), new WordToDoublequotedStringQuickfix(word));
                    problemsHolder.registerProblem(word, getShortName(), new WordToSinglequotedStringQuickfix(word));
                }
            }
        };
    }
}
