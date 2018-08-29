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

package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashParameterExpansion;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.ElementPatternCondition;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.Nullable;

/**
 * Simple completion provider which adds the Bash keywords as boldened completion options.
 */
class BashKeywordCompletionProvider extends AbstractBashCompletionProvider {
    private static final ElementPattern<PsiElement> KEYWORD_PATTERN = new KeywordElementPattern();

    void addTo(CompletionContributor contributor) {
        contributor.extend(CompletionType.BASIC, KEYWORD_PATTERN, this);
    }

    @Override
    protected void addBashCompletions(String currentText, CompletionParameters parameters, ProcessingContext context, CompletionResultSet resultWithoutPrefix) {
        if (currentText != null && currentText.startsWith("$")) {
            return;
        }

        for (String keyword : LanguageBuiltins.completionKeywords) {
            resultWithoutPrefix.addElement(LookupElementBuilder.create(keyword).bold());
        }
    }

    private static class KeywordElementPattern implements ElementPattern<PsiElement> {
        private static final TokenSet rejectedTokens = TokenSet.create(BashTokenTypes.VARIABLE);

        @Override
        public boolean accepts(@Nullable Object o) {
            return false;
        }

        @Override
        public boolean accepts(@Nullable Object o, ProcessingContext context) {
            if (o instanceof LeafPsiElement && rejectedTokens.contains(((LeafPsiElement) o).getElementType())) {
                return false;
            }

            if (o instanceof PsiElement) {
                if (BashPsiUtils.hasParentOfType((PsiElement) o, BashString.class, 5)) {
                    return false;
                }

                if (BashPsiUtils.hasParentOfType((PsiElement) o, PsiComment.class, 3)) {
                    return false;
                }

                if (BashPsiUtils.hasParentOfType((PsiElement) o, BashShebang.class, 3)) {
                    return false;
                }

                if (BashPsiUtils.hasParentOfType((PsiElement) o, BashHereDoc.class, 1)) {
                    return false;
                }

                if (BashPsiUtils.hasParentOfType((PsiElement) o, BashParameterExpansion.class, 2)) {
                    return false;
                }

                if (BashPsiUtils.isCommandParameterWord((PsiElement) o)) {
                    return false;
                }

                return true;

            }

            return false;
        }

        @Override
        public ElementPatternCondition<PsiElement> getCondition() {
            return null;
        }
    }
}
