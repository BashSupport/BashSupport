package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarProcessor;
import com.ansorgit.plugins.bash.lang.psi.impl.word.InjectionUtils;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludeCommandIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashInjectionVarDefinitionsIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashVarDefIndex;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class BashResolveUtil {
    private BashResolveUtil() {
    }

    public static GlobalSearchScope varDefSearchScope(BashVar reference, boolean withIncludedFiles) {
        PsiFile referenceFile = BashPsiUtils.findFileContext(reference, true);
        if (!withIncludedFiles) {
            return GlobalSearchScope.fileScope(referenceFile.getProject(), referenceFile.getVirtualFile());
        }

        Set<VirtualFile> result = Sets.newLinkedHashSet();
        result.add(referenceFile.getVirtualFile());

        int referenceFileOffset = BashPsiUtils.getFileTextOffset(reference);
        BashFunctionDef referenceFunctionContainer = BashPsiUtils.findNextVarDefFunctionDefScope(reference);

        for (BashIncludeCommand command : BashPsiUtils.findIncludeCommands(referenceFile, null)) {
            boolean includeIsInFunction = BashPsiUtils.findNextVarDefFunctionDefScope(command) != null;

            //either one of var or include command is in a function or the var is used after the include command
            if (referenceFunctionContainer != null || includeIsInFunction || (referenceFileOffset > BashPsiUtils.getFileTextEndOffset(command))) {
                BashFileReference fileReference = command.getFileReference();
                PsiFile includedFile = fileReference != null ? fileReference.findReferencedFile() : null;
                if (includedFile != null) {
                    result.add(includedFile.getVirtualFile());

                    //also, add all files included in the valid include command's file
                    for (PsiFile file : BashPsiUtils.findIncludedFiles(includedFile, true)) {
                        result.add(file.getVirtualFile());
                    }
                }
            }
        }

        return GlobalSearchScope.filesScope(referenceFile.getProject(), result);
    }


    public static void walkVariableDefinitions(@NotNull BashVar reference, @NotNull Function<BashVarDef, Boolean> varDefProcessor) {
        String varName = reference.getName();
        if (StringUtils.isBlank(varName)) {
            return;
        }

        Project project = reference.getProject();
        GlobalSearchScope filesScope = varDefSearchScope(reference, true);
        PsiElement referenceDefinition = reference.getReference().resolve();
        if (referenceDefinition == null) {
            return;
        }

        for (BashVarDef candidate : StubIndex.getElements(BashVarDefIndex.KEY, varName, project, filesScope, BashVarDef.class)) {
            //only variables which have the same original definition should be processed
            //e.g. local variables won't be processed this way if a global variable is given to this method
            if (referenceDefinition.isEquivalentTo(candidate) || referenceDefinition.isEquivalentTo(candidate.getReference().resolve())) {
                Boolean walkOn = varDefProcessor.apply(candidate);
                if (walkOn == null || !walkOn) {
                    return;
                }
            }
        }

        //process the injected variable definitions
        Collection<BashLanguageInjectionHost> injectionsHosts = StubIndex.getElements(BashInjectionVarDefinitionsIndex.KEY, varName, project, filesScope, BashLanguageInjectionHost.class);
        for (BashLanguageInjectionHost host : injectionsHosts) {
            List<BashVarDef> varDefs = InjectionUtils.collectVariableDefinitions(host);

            for (BashVarDef candidate : varDefs) {
                if (referenceDefinition.isEquivalentTo(candidate) || referenceDefinition.isEquivalentTo(candidate.getReference().resolve())) {
                    Boolean walkOn = varDefProcessor.apply(candidate);
                    if (walkOn == null || !walkOn) {
                        return;
                    }
                }
            }
        }
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
        for (BashVarDef varDef : varDefs) {
            processor.execute(varDef, resolveState);
        }

        walkInjectionHosts(bashVar, varName, project, resolveState, processor, fileScope);

        if (filePath != null) {
            Collection<BashIncludeCommand> includeCommands = StubIndex.getElements(BashIncludeCommandIndex.KEY, filePath, project, fileScope, BashIncludeCommand.class);
        if (!includeCommands.isEmpty()) {
            boolean varIsInFunction = BashPsiUtils.findNextVarDefFunctionDefScope(bashVar) != null;

            for (BashIncludeCommand command : includeCommands) {
                boolean includeIsInFunction = BashPsiUtils.findNextVarDefFunctionDefScope(command) != null;

                //either one of var or include command is in a function or the var is used after the include command
                if (varIsInFunction || includeIsInFunction || (BashPsiUtils.getFileTextOffset(bashVar) > BashPsiUtils.getFileTextEndOffset(command))) {
                    command.processDeclarations(processor, resolveState, command, bashVar);

                        BashFileReference fileReference = command.getFileReference();
                        PsiFile includedFile = fileReference != null ? fileReference.findReferencedFile() : null;
                        if (includedFile != null) {
                            walkInjectionHosts(bashVar, varName, project, resolveState, processor, GlobalSearchScope.fileScope(includedFile));
                        }
                    }
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
