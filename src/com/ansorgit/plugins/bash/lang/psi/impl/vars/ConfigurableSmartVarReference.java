package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * ConfigurableSmartVarReference allows to set 'preferNeighbourhood' parameter 
 * when code searches a reference via {@link BashResolveUtil#resolve(BashVar, boolean, boolean, boolean)}.
 * 
 * @author Nosov Aleksandr <nosovae.dev@gmail.com>
 */
public class ConfigurableSmartVarReference extends AbstractBashVarReference {
    private boolean preferNeighbourhood;

    public ConfigurableSmartVarReference(BashReference delegate, boolean preferNeighbourhood) {
        super(((AbstractBashVarReference) delegate).bashVar);
        this.preferNeighbourhood = preferNeighbourhood;
    }

    @Nullable
    @Override
    public PsiElement resolveInner() {
        return BashResolveUtil.resolve(bashVar, true, false, preferNeighbourhood);
    }
}
