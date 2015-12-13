package com.ansorgit.plugins.bash.lang.psi.api;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarUse;

import java.util.List;

/**
 * Marks elements which may provide language injection for the Bash language.
 */
public interface BashLanguageInjectionHost extends BashCharSequence {
    boolean isValidBashLanguageHost();

    List<BashVarUse> getVariableUses();

    List<BashVarDef> getVariableDefinitions();
}
