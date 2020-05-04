package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.psi.impl.command.*;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.*;
import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class BashReferenceContributor extends PsiReferenceContributor {
    private final ElementPattern<? extends PsiElement> bashVar = StandardPatterns.instanceOf(BashVarImpl.class);
    private final ElementPattern<? extends PsiElement> bashVarDef = StandardPatterns.instanceOf(BashVarDefImpl.class);
    private final ElementPattern<? extends PsiElement> bashCommand = StandardPatterns.instanceOf(AbstractBashCommand.class);

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(bashVar, new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                assert element instanceof BashVarImpl;

                if (DumbService.isDumb(element.getProject())) {
                    return new PsiReference[]{new DumbBashVarReference((BashVarImpl)element)};
                }
                return new PsiReference[]{new SmartBashVarReference((BashVarImpl)element)};
            }
        });

        registrar.registerReferenceProvider(bashVarDef, new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                assert element instanceof BashVarDefImpl;
                if (DumbService.isDumb(element.getProject())) {
                    return new PsiReference[]{new DumbVarDefReference((BashVarDefImpl)element)};
                }
                return new PsiReference[]{new SmartVarDefReference((BashVarDefImpl)element)};
            }
        });

        registrar.registerReferenceProvider(bashCommand, new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                assert element instanceof AbstractBashCommand;
                AbstractBashCommand<?> command = (AbstractBashCommand<?>)element;

                if (isSlowResolveRequired(element)) {
                    return new PsiReference[]{new DumbFunctionReference(command), new DumbBashFileReference(command)};
                }
                return new PsiReference[]{new SmartFunctionReference(command), new SmartBashFileReference(command)};
            }
        });
    }

    /**
     * @return returns whether the file containing this command is indexed or whether a slow fallback is required to resolve the references contained in the file.
     */
    private boolean isSlowResolveRequired(PsiElement e) {
        Project project = e.getProject();
        PsiFile file = e.getContainingFile();

        return DumbService.isDumb(project) ||
               BashResolveUtil.isScratchFile(file) ||
               BashResolveUtil.isNotIndexedFile(project, file.getVirtualFile());
    }
}
