package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * Variable reference implementation for smart mode.
 *
 * @author jansorg
 */
class SmartBashVarReference extends AbstractBashVarReference {

    public SmartBashVarReference(BashVarImpl bashVar) {
        super(bashVar);
    }

    @Nullable
    @Override
    public PsiElement resolveInner() {
        return BashResolveUtil.resolve(bashVar, true, false);
    }

}
