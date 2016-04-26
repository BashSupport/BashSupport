/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenamePsiFileProcessor;
import com.intellij.util.Query;
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
