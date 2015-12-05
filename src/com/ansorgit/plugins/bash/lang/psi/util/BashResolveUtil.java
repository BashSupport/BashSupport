package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarProcessor;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludeCommandIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashVarDefIndex;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.cache.impl.BaseFilterLexerUtil;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class BashResolveUtil {
    private BashResolveUtil() {
    }

    public static PsiElement resolve(BashVar bashVar, boolean leaveInjectionHosts) {
        if (bashVar == null || !bashVar.isPhysical()) {
            return null;
        }

        final String varName = bashVar.getReferenceName();
        if (varName == null) {
            return null;
        }

        PsiFile psiFile = BashPsiUtils.findFileContext(bashVar, true);
        VirtualFile virtualFile = psiFile.getVirtualFile();

        String filePath = virtualFile != null ? virtualFile.getPath() : null;
        Project project = bashVar.getProject();

        ResolveState resolveState = ResolveState.initial();
        ResolveProcessor processor = new BashVarProcessor(bashVar, varName, true, leaveInjectionHosts);

        GlobalSearchScope fileScope = GlobalSearchScope.fileScope(psiFile);

        Collection<BashVarDef> varDefs = StubIndex.getElements(BashVarDefIndex.KEY, varName, project, fileScope, BashVarDef.class);

        Collection<BashIncludeCommand> includeCommands = filePath != null
                ? StubIndex.getElements(BashIncludeCommandIndex.KEY, filePath, project, fileScope, BashIncludeCommand.class)
                : Collections.<BashIncludeCommand>emptySet();

        for (BashVarDef varDef : varDefs) {
            processor.execute(varDef, resolveState);
        }

        if (!includeCommands.isEmpty()) {
            boolean varIsInFunction = BashPsiUtils.findNextVarDefFunctionDefScope(bashVar) != null;

            for (BashIncludeCommand command : includeCommands) {
                boolean includeIsInFunction = BashPsiUtils.findNextVarDefFunctionDefScope(command) != null;

                //either one of var or include command is in a function or the var is used after the include command
                if (varIsInFunction || includeIsInFunction || (BashPsiUtils.getFileTextOffset(bashVar) > BashPsiUtils.getFileTextEndOffset(command))) {
                    command.processDeclarations(processor, resolveState, command, bashVar);
                }
            }
        }

        processor.prepareResults();
        return processor.getBestResult(false, bashVar);
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

        //fixme use stubs
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
