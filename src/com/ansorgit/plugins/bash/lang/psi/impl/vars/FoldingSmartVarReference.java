package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import static com.intellij.util.containers.ContainerUtil.newArrayList;

import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.analyze.CaseBlockDetector;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.analyze.IfBlockDetector;
import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * {@link FoldingSmartVarReference} allows to set 'preferNeighbourhood' parameter and set some detectors 
 * when execute reference search via {@link BashResolveUtil#resolve(BashVar, boolean, ResolveProcessor)}.
 *
 * @see com.ansorgit.plugins.bash.lang.psi.impl.vars.analyze.AmbiguousVarDefDetector
 * @author Nosov Aleksandr <nosovae.dev@gmail.com>
 */
public class FoldingSmartVarReference extends AbstractBashVarReference {
    private final boolean preferNeighbourhood;

    public FoldingSmartVarReference(BashReference delegate, boolean preferNeighbourhood) {
        super(((AbstractBashVarReference) delegate).bashVar);
        this.preferNeighbourhood = preferNeighbourhood;
    }

    @Nullable
    @Override
    public PsiElement resolveInner() {
        final String varName = bashVar.getReferenceName();
        if (varName == null) {
            return null;
        }

        return BashResolveUtil.resolve(bashVar, true,
                new FoldingBashVarProcessor(bashVar, varName, preferNeighbourhood,
                        newArrayList(new IfBlockDetector(), new CaseBlockDetector())));
    }
}
