package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarProcessor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;

public final class BashResolveUtil {
    private BashResolveUtil() {
    }

    public static PsiElement resolve(BashVarImpl bashVar, boolean leaveInjectionHosts) {
        if (bashVar == null) {
            return null;
        }

        final String varName = bashVar.getReferencedName();
        if (varName == null) {
            return null;
        }

        ResolveProcessor processor = new BashVarProcessor(bashVar, varName, true, leaveInjectionHosts);
        PsiFile containingFile = BashPsiUtils.findFileContext(bashVar, true);

        if (!BashPsiUtils.varResolveTreeWalkUp(processor, bashVar, containingFile, ResolveState.initial())) {
            return processor.getBestResult(false, bashVar);
        }

        return null;
    }
}
