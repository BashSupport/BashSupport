/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFindUsagesProvider.java, Class: BashFindUsagesProvider
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

package com.ansorgit.plugins.bash.editor.usages;

import com.ansorgit.plugins.bash.lang.lexer.BashLexer;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocMarker;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * The find usages provider implementation for Bash.
 * <p/>
 * Date: 06.05.2009
 * Time: 20:42:06
 *
 * @author Joachim Ansorg
 */
public class BashFindUsagesProvider implements FindUsagesProvider, BashTokenTypes {
    private static final class BashWordsScanner extends DefaultWordsScanner {
        private static final TokenSet literals = TokenSet.create(BashElementTypes.STRING_ELEMENT, STRING2, INTEGER_LITERAL);

        public BashWordsScanner() {
            super(new BashLexer(), identifierTokenSet, BashTokenTypes.comments, literals);
            setMayHaveFileRefsInLiterals(true);
        }
    }

    public WordsScanner getWordsScanner() {
        return new BashWordsScanner();
    }

    public boolean canFindUsagesFor(@NotNull PsiElement psi) {
        return psi instanceof BashVar || psi instanceof BashCommand || psi instanceof BashHereDocMarker;
    }

    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    public String getType(@NotNull PsiElement element) {
        if (element instanceof BashFunctionDef) {
            return "function";
        }
        if (element instanceof BashVarDef) {
            return "variable";
        }
        if (element instanceof BashHereDocMarker) {
            return "heredoc marker";
        }

        return "unknown type";
    }

    @NotNull
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (!canFindUsagesFor(element)) {
            return "";
        }

        String name = ((PsiNamedElement) element).getName();
        return name != null ? name : "";
    }

    @NotNull
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}
