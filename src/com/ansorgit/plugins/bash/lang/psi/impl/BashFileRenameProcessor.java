package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.rename.RenamePsiFileProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * This rename processor handles file renamed.
 * IntelliJ has a setting to disable filename renames if disabled. This is dangerous for Bash and thus we need to override the
 * setting.
 *
 * @author jansorg
 */
public class BashFileRenameProcessor extends RenamePsiFileProcessor {
    @NotNull
    @Override
    public Collection<PsiReference> findReferences(PsiElement element) {
        SearchScope scope = BashElementSharedImpl.getElementUseScope((BashPsiElement) element, element.getProject());
        return ReferencesSearch.search(element, scope).findAll();
    }

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return (element instanceof PsiFile || element instanceof BashFileReference);
    }
}
