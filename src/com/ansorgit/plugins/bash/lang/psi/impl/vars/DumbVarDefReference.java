package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author jansorg
 */
class DumbVarDefReference extends AbstractVarDefReference {
    public DumbVarDefReference(BashVarDefImpl bashVarDef) {
        super(bashVarDef);
    }

    @Nullable
    @Override
    public PsiElement resolveInner() {
        if (bashVarDef.isCommandLocal()) {
            return null;
        }

        return BashResolveUtil.resolve(bashVarDef, true, true);

    }
}
