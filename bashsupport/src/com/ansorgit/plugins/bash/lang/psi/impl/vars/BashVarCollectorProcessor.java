package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.psi.scope.PsiScopeProcessor;

import java.util.List;

/**
 * User: jansorg
 * Date: 07.02.11
 * Time: 19:55
 */
public interface BashVarCollectorProcessor extends PsiScopeProcessor {
    List<BashVarDef> getVariables();
}
