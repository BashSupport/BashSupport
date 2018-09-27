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

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashCharSequence;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BashFileReferenceImpl extends BashBaseElement implements BashFileReference {
    public BashFileReferenceImpl(final ASTNode astNode) {
        super(astNode, "Bash File reference");
    }

    private final PsiReference fileReference = new CachingFileReference(this);

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
        return fileReference;
    }

    @Override
    public boolean canNavigate() {
        return fileReference.resolve() != null;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitFileReference(this);
        } else {
            visitor.visitElement(this);
        }
    }

    private static class CachingFileReference extends CachingReference implements PsiReference, BindablePsiReference, PsiFileReference {
        private final BashFileReferenceImpl element;

        public CachingFileReference(BashFileReferenceImpl myElement) {
            this.element = myElement;
        }

        @Override
        public BashFileReferenceImpl getElement() {
            return element;
        }

        public TextRange getRangeInElement() {
            return getManipulator().getRangeInElement(element);
        }

        @NotNull
        private ElementManipulator<BashFileReferenceImpl> getManipulator() {
            ElementManipulator<BashFileReferenceImpl> manipulator = ElementManipulators.getManipulator(element);
            if (manipulator == null) {
                throw new IncorrectOperationException("no implementation found to rename " + element);
            }
            return manipulator;
        }

        @NotNull
        public String getCanonicalText() {
            return this.element.getText();
        }

        public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
            ElementManipulator<BashFileReferenceImpl> manipulator = getManipulator();

            return manipulator.handleContentChange(element, newName);
        }

        public PsiElement bindToElement(@NotNull PsiElement targetElement) throws IncorrectOperationException {
            if (targetElement instanceof PsiFile) {
                //findRelativePath already leaves the injection host file
                PsiFile currentFile = BashPsiUtils.findFileContext(this.element);
                String relativeFilePath = BashPsiFileUtils.findRelativeFilePath(currentFile, (PsiFile) targetElement);

                return handleElementRename(relativeFilePath);
            }

            throw new IncorrectOperationException("Unsupported for element type " + targetElement);
        }

        public boolean isReferenceTo(PsiElement element) {
            return PsiManager.getInstance(element.getProject()).areElementsEquivalent(element, resolve());
        }

        @NotNull
        public Object[] getVariants() {
            return PsiElement.EMPTY_ARRAY;
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean incompleteCode) {
            PsiElement resolve = resolve();
            if (resolve == null) {
                return ResolveResult.EMPTY_ARRAY;
            }

            return new ResolveResult[]{new PsiElementResolveResult(resolve, true)};
        }

        @Nullable
        public PsiElement resolveInner() {
            PsiFile containingFile = BashPsiUtils.findFileContext(getElement());
            if (!containingFile.isPhysical()) {
                return null;
            }

            return BashPsiFileUtils.findRelativeFile(containingFile, element.getFilename());
        }
    }
}
