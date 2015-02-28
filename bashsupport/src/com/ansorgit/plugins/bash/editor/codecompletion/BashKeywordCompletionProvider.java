package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashParameterExpansion;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.ElementPatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.Nullable;

/**
 * Simple completion provider which adds the Bash keywords as boldened completion options.
 */
class BashKeywordCompletionProvider extends AbstractBashCompletionProvider {
    private static final ElementPattern<PsiElement> KEYWORD_PATTERN = new ElementPattern<PsiElement>() {
        @Override
        public boolean accepts(@Nullable Object o) {
            return false;
        }

        @Override
        public boolean accepts(@Nullable Object o, ProcessingContext context) {
            if (o instanceof PsiElement) {
                if (BashPsiUtils.hasParentOfType((PsiElement) o, BashString.class, 5)) {
                    return false;
                }

                if (BashPsiUtils.hasParentOfType((PsiElement) o, BashParameterExpansion.class, 2)) {
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
    };

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
}
