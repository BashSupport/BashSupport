package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.codeInsight.completion.CompletionLocation;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.CompletionWeigher;
import com.intellij.codeInsight.lookup.LookupElement;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This weigher is used to move prefered variable completions to the top of the suggested items.
 * <p/>
 * User: jansorg
 * Date: 07.02.11
 * Time: 18:59
 */
public class BashVariableCompletionWeigher extends CompletionWeigher {
    enum MyResult {
        localVariable,
        fileVariable,
        includedVariable,
        globalVariable,
        buildInVariable,
        normal,
    }

    public MyResult weigh(@NotNull final LookupElement item, @NotNull final CompletionLocation location) {
        if (location == null) {
            return null;
        }

        final Object object = item.getObject();

        if (location.getCompletionType() == CompletionType.BASIC) {
            if (object instanceof BashVarDef) {
                BashVarDef varDef = (BashVarDef) object;
                if (varDef.isFunctionScopeLocal()) {
                    return MyResult.localVariable;
                }

                if (!varDef.getContainingFile().equals(location.getCompletionParameters().getOriginalFile())) {
                    return MyResult.includedVariable;
                }

                return MyResult.fileVariable;
            }

            if (object instanceof String) {
                if (LanguageBuiltins.bashShellVars.contains(object.toString())) {
                    return MyResult.buildInVariable;
                }

                Set<String> globalVariables = BashProjectSettings.storedSettings(location.getProject()).getGlobalVariables();
                if (globalVariables.contains(object.toString())) {
                    return MyResult.globalVariable;
                }
            }
        }

        return MyResult.normal;
    }
}
