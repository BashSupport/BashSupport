/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFileReferenceImpl.java, Class: BashFileReferenceImpl
 * Last modified: 2013-05-02
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashCharSequence;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.Function;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class BashFileReferenceImpl extends BashBaseElement implements BashFileReference, PsiNamedElement {
    private FileReferenceSet fileReferenceSet;

    public BashFileReferenceImpl(final ASTNode astNode) {
        super(astNode, "Bash File reference");
    }

    @Nullable
    @Override
    public PsiFile findReferencedFile() {
        PsiReference reference = getReference();
        if (reference == null) {
            return null;
        }

        return (PsiFile) reference.resolve();
    }

    @NotNull
    public String getFilename() {
        PsiElement firstParam = getFirstChild();
        if (firstParam instanceof BashCharSequence) {
            return ((BashCharSequence) firstParam).getUnwrappedCharSequence();
        }

        return getText();
    }

    public boolean isStatic() {
        PsiElement firstChild = getFirstChild();
        return firstChild instanceof BashCharSequence && ((BashCharSequence) firstChild).isStatic();
    }

    @Override
    public PsiReference getReference() {
        return new CachingFileReference(this);
        //fixme
        //throw new IllegalStateException("not implemeneted");
        //return referenceSet().getLastReference();
    }

    @Override
    public boolean canNavigate() {
        return getReference().resolve() != null;
    }

    @Override
    public boolean canNavigateToSource() {
        return super.canNavigateToSource();
    }

    private FileReferenceSet referenceSet() {
        if (fileReferenceSet == null) {
            synchronized (this) {
                fileReferenceSet = new FileReferenceSet(this);
                fileReferenceSet.addCustomization(FileReferenceSet.DEFAULT_PATH_EVALUATOR_OPTION, new Function<PsiFile, Collection<PsiFileSystemItem>>() {
                    @Override
                    public Collection<PsiFileSystemItem> fun(PsiFile psiFile) {
                        return Collections.<PsiFileSystemItem>singletonList(psiFile.getContainingDirectory());
                    }
                });
            }
        }

        return fileReferenceSet;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitFileReference(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
    }

    @Override
    protected void clearUserData() {
        super.clearUserData();
    }

    @Override
    public PsiElement replace(@NotNull PsiElement newElement) throws IncorrectOperationException {
        return super.replace(newElement);
    }

    @Override
    public String getName() {
        return getFilename();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        PsiReference lastReference = getReference();
        if (lastReference == null) {
            throw new IncorrectOperationException("no reference found to handle element rename");
        }

        return lastReference.handleElementRename(name);
    }

    private static class CachingFileReference extends PsiReferenceBase<BashFileReferenceImpl> implements PsiReference, BindablePsiReference, PsiFileReference {
        private final BashFileReferenceImpl referencedFile;

        public CachingFileReference(BashFileReferenceImpl referencedFile) {
            super(referencedFile, false);
            this.referencedFile = referencedFile;
        }

        @Override
        public BashFileReferenceImpl getElement() {
            return referencedFile;
        }

        public TextRange getRangeInElement() {
            PsiElement firstChild = referencedFile.getFirstChild();

            if (firstChild instanceof BashCharSequence) {
                return ((BashCharSequence) firstChild).getTextContentRange();
            }

            return TextRange.from(0, referencedFile.getTextLength());
        }

        @NotNull
        public String getCanonicalText() {
            return this.referencedFile.getText();
        }

        public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
            PsiElement firstChild = referencedFile.getFirstChild();

            String name;
            if (firstChild instanceof BashCharSequence) {
                name = ((BashCharSequence) firstChild).createEquallyWrappedString(newName);
            } else {
                name = newName;c
            }

            return BashPsiUtils.replaceElement(referencedFile, BashPsiElementFactory.createFileReference(referencedFile.getProject(), name));
        }

        public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
            if (element instanceof PsiFile) {
                return handleElementRename(((PsiFile) element).getName());
            }

            throw new IncorrectOperationException("unsupported for psi element " + element);
        }

        public boolean isReferenceTo(PsiElement element) {
            PsiFile containingFile = referencedFile.getContainingFile();
            return element == this || element.equals(BashPsiFileUtils.findRelativeFile(containingFile, referencedFile.getFilename()));
        }

        @NotNull
        public Object[] getVariants() {
            return PsiElement.EMPTY_ARRAY;
        }


        @Override
        public PsiElement resolve() {
            return resolveInner();
        }

        @Nullable
        public PsiElement resolveInner() {
            PsiFile containingFile = BashPsiUtils.findFileContext(getElement());
            return BashPsiFileUtils.findRelativeFile(containingFile, referencedFile.getFilename());
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean incompleteCode) {
            PsiElement element = resolve();

            return element != null ? new ResolveResult[]{new PsiElementResolveResult(element)} : ResolveResult.EMPTY_ARRAY;
        }
    }
}
