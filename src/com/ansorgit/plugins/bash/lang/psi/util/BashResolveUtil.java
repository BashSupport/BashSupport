package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarProcessor;
import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    public static boolean processContainerDeclarations(PsiElement thisElement, @NotNull final PsiScopeProcessor processor, @NotNull final ResolveState state, final PsiElement lastParent, @NotNull final PsiElement place) {
        if (thisElement == lastParent) {
            return true;
        }

        if (!processor.execute(thisElement, state)) {
            return false;
        }

        //process the current's elements children from first until lastParent is reached, a definition has to be before the use
        List<PsiElement> functions = Lists.newLinkedList();

        for (PsiElement child = thisElement.getFirstChild(); child != null && child != lastParent; child = child.getNextSibling()) {
            if (child instanceof BashFunctionDef) {
                functions.add(child);
            } else if (!child.processDeclarations(processor, state, lastParent, place)) {
                return false;
            }
        }

        for (PsiElement function : functions) {
            if (!function.processDeclarations(processor, state, lastParent, place)) {
                return false;
            }
        }

        //the definition has not been found yet, we have to process the function contained in thisElement

        //if the start element is contained in a function then all functions contained in thisElement need to be processed
        //if the start element is not contained in a function then only functions prior to the start element need to be processed

/*
        if (lastParent != null && BashPsiUtils.findParent(place, BashFunctionDef.class) != null) {
            for (PsiElement child = lastParent.getNextSibling(); child != null; child = child.getNextSibling()) {
                if (thisElement != child && !child.processDeclarations(processor, state, child, place)) {
                    return false;
                }
            }
        }
        */

        return true;
    }
}
