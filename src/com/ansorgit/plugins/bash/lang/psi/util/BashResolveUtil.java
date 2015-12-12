package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarProcessor;
import com.ansorgit.plugins.bash.lang.psi.impl.word.InjectionUtils;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludeCommandIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashVarDefIndex;
import com.google.common.collect.Lists;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

        //the stub index doesn't include injected elements
        Collection<BashLanguageInjectionHost> hosts = PsiTreeUtil.findChildrenOfType(psiFile, BashLanguageInjectionHost.class);
        for (BashLanguageInjectionHost host : hosts) {
            if (host.isValidBashLanguageHost()) {
                InjectionUtils.walkInjection(host, processor, resolveState, null, bashVar, true);
            }
        }

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

    private static List<BashVarDef> collectInjectedVarDefs(PsiFile psiFile) {
        ArrayList<BashVarDef> varDefs = Lists.newArrayList();
        Collection<BashLanguageInjectionHost> hosts = PsiTreeUtil.findChildrenOfType(psiFile, BashLanguageInjectionHost.class);

        for (BashLanguageInjectionHost host : hosts) {
            if (host.isValidBashLanguageHost()) {
                List<Pair<PsiElement, TextRange>> injected = InjectedLanguageManager.getInstance(host.getProject()).getInjectedPsiFiles(host);

                if (injected != null) {
                    for (Pair<PsiElement, TextRange> pair : injected) {
                        Collection<BashVarDef> defs = PsiTreeUtil.findChildrenOfType(pair.first, BashVarDef.class);

                        varDefs.addAll(defs);
                    }
                }
            }
        }

        return varDefs;
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
        if (lastParent != null && lastParent.getParent().isEquivalentTo(thisElement) && BashPsiUtils.findNextVarDefFunctionDefScope(place) != null) {
            for (PsiElement sibling = lastParent.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
                if (!sibling.processDeclarations(processor, state, null, place)) {
                    return false;
                }
            }
        }

        return true;
    }
}
