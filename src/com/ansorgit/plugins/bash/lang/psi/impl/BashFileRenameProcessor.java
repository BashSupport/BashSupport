package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.RefactoringSettings;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenamePsiFileProcessor;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * This rename processor handles file renamed.
 * IntelliJ has a setting to disable filename renames if disabled. This is dangerous for Bash and thus we need to override the
 * setting.
 *
 * @author jansorg
 */
public class BashFileRenameProcessor extends RenamePsiFileProcessor {
    @Override
    public RenameDialog createRenameDialog(Project project, PsiElement element, PsiElement nameSuggestionContext, Editor editor) {
        return new BashFileRenameDialog(project, element, nameSuggestionContext, editor);
    }

    /**
     * Returns references to the given element. If it is a BashPsiElement a special search scope is used to locate the elements referencing the file.
     *
     * @param element References to the given element
     * @return
     */
    @NotNull
    @Override
    public Collection<PsiReference> findReferences(PsiElement element) {
        //fixme fix the custom scope
        SearchScope scope = (element instanceof BashPsiElement)
                ? BashElementSharedImpl.getElementUseScope((BashPsiElement) element, element.getProject())
                : GlobalSearchScope.projectScope(element.getProject());

        Query<PsiReference> search = ReferencesSearch.search(element, scope);
        return search.findAll();
    }

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return (element instanceof BashFile);
    }

    private static class BashFileRenameDialog extends RenameDialog {
        public BashFileRenameDialog(Project project, PsiElement element, PsiElement nameSuggestionContext, Editor editor) {
            super(project, element, nameSuggestionContext, editor);

            setTitle("Rename Bash file");
        }
    }
}
