/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFindUsagesProvider.java, Class: BashFindUsagesProvider
 * Last modified: 2013-05-02
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
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocMarker;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIndexVersion;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Processor;
import org.apache.commons.lang.StringUtils;
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
        private static final TokenSet literals = TokenSet.create(BashElementTypes.STRING_ELEMENT, STRING2, INTEGER_LITERAL, WORD, STRING_CHAR);
        private static final TokenSet identifiers = TokenSet.create(VARIABLE);

        public BashWordsScanner() {
            super(new BashLexer(), identifiers, BashTokenTypes.commentTokens, literals);
            setMayHaveFileRefsInLiterals(true);
        }

        @Override
        public void processWords(CharSequence fileText, Processor<WordOccurrence> processor) {
            super.processWords(fileText, processor);
        }

        @Override
        public int getVersion() {
            return BashIndexVersion.ID_INDEX_VERSION + super.getVersion();
        }
    }

    public WordsScanner getWordsScanner() {
        return new BashWordsScanner();
    }

    public boolean canFindUsagesFor(@NotNull PsiElement psi) {
        return psi instanceof BashVar
                || psi instanceof BashFile
                || psi instanceof BashFileReference
                || psi instanceof BashCommand
                || psi instanceof BashHereDocMarker
                || psi instanceof BashFunctionDef;
    }

    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    public String getType(@NotNull PsiElement element) {
        if (element instanceof BashFunctionDef) {
            return "function";
        }

        if (element instanceof BashCommand) {
            if (((BashCommand) element).isFunctionCall()) {
                return "function";
            }

            if (((BashCommand) element).isBashScriptCall()) {
                return "Bash script call";
            }

            return "command";
        }

        if (element instanceof BashVarDef) {
            return "variable";
        }

        if (element instanceof BashHereDocMarker) {
            return "heredoc marker";
        }

        if (element instanceof BashFile) {
            return "Bash file";
        }

        return "unknown type";
    }

    @NotNull
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (!canFindUsagesFor(element)) {
            return "";
        }

        if (element instanceof BashCommand) {
            return StringUtils.stripToEmpty(((BashCommand) element).getReferencedCommandName());
        }

        if (element instanceof PsiNamedElement) {
            return StringUtils.stripToEmpty(((PsiNamedElement) element).getName());
        }

        //fixme
        return "";
    }

    @NotNull
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}
