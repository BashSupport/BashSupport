package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.lang.psi.FileInclusionManager;
import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarProcessor;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludeCommandIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashVarDefIndex;
import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public final class BashResolveUtil {
    private BashResolveUtil() {
    }

    public static PsiElement resolve(BashVarImpl bashVar, boolean leaveInjectionHosts) {
        if (bashVar == null || !bashVar.isPhysical()) {
            return null;
        }

        final String varName = bashVar.getReferencedName();
        if (varName == null) {
            return null;
        }

        ResolveState resolveState = ResolveState.initial();
        ResolveProcessor processor = new BashVarProcessor(bashVar, varName, true, leaveInjectionHosts);

        GlobalSearchScope fileScope = GlobalSearchScope.fileScope(bashVar.getContainingFile());
        Collection<BashVarDef> varDefs = StubIndex.getElements(BashVarDefIndex.KEY, varName, bashVar.getProject(), fileScope, BashVarDef.class);
        Collection<BashIncludeCommand> includeCommands = StubIndex.getElements(BashIncludeCommandIndex.KEY, bashVar.getContainingFile().getVirtualFile().getCanonicalPath(), bashVar.getProject(), fileScope, BashIncludeCommand.class);

        for (BashVarDef varDef : varDefs) {
            processor.execute(varDef, resolveState);
        }

        for (BashIncludeCommand command : includeCommands) {
            //processor.execute(command, resolveState);
            command.processDeclarations(processor, resolveState, command, bashVar);
        }

        return processor.getBestResult(false, bashVar);

        /*
        PsiFile containingFile = BashPsiUtils.findFileContext(bashVar, true);

        if (!BashPsiUtils.varResolveTreeWalkUp(processor, bashVar, containingFile, ResolveState.initial())) {
            return processor.getBestResult(false, bashVar);
        }

        return null;
        */
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

        //fixme this is very slow atm
        if (lastParent != null && lastParent.getParent() == thisElement && BashPsiUtils.findNextVarDefFunctionDefScope(place) != null) {
            for (PsiElement sibling = lastParent.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
                if (!sibling.processDeclarations(processor, state, null, place)) {
                    return false;
                }
            }
        }

        return true;
    }
}
