package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * Variable reference implementation which works in dumb mode.
 *
 * @author jansorg
 */
class DumbBashVarReference extends AbstractBashVarReference {
    public DumbBashVarReference(BashVarImpl bashVar) {
        super(bashVar);
    }

    @Nullable
    @Override
    public PsiElement resolveInner() {
        return BashResolveUtil.resolve(bashVar, true, true);
    }
}
