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
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BashFileReferenceImpl extends BashBaseStubElementImpl<StubElement> implements BashFileReference {
    private PsiReference cachingReference;

    public BashFileReferenceImpl(final ASTNode astNode) {
        super(astNode, "File reference");
        this.cachingReference = new CachingFileReference(this);
    }

    @Nullable
    @Override
    public PsiFile findReferencedFile() {
        return (PsiFile) cachingReference.resolve();
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
        return cachingReference;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitFileReference(this);
        } else {
            visitor.visitElement(this);
        }
    }


    private static class CachingFileReference extends CachingReference {
        private final BashFileReferenceImpl fileReference;

        public CachingFileReference(BashFileReferenceImpl fileReference) {
            this.fileReference = fileReference;
        }

        @Override
        public PsiElement getElement() {
            return fileReference;
        }

        public TextRange getRangeInElement() {
            PsiElement firstChild = fileReference.getFirstChild();

            if (firstChild instanceof BashCharSequence) {
                return ((BashCharSequence) firstChild).getTextContentRange();
            }

            return TextRange.from(0, fileReference.getTextLength());
        }

        @NotNull
        public String getCanonicalText() {
            return this.fileReference.getText();
        }

        public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
            return BashPsiUtils.replaceElement(fileReference, BashChangeUtil.createWord(fileReference.getProject(), newName));
        }

        public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
            throw new IncorrectOperationException("not supported");
        }

        public boolean isReferenceTo(PsiElement element) {
            PsiFile containingFile = this.fileReference.getContainingFile();
            return element == this || element.equals(BashPsiFileUtils.findRelativeFile(containingFile, this.fileReference.getFilename()));
        }

        @NotNull
        public Object[] getVariants() {
            return PsiElement.EMPTY_ARRAY;
        }


        @Nullable
        @Override
        public PsiElement resolveInner() {
            PsiFile containingFile = BashPsiUtils.findFileContext(getElement());
            return BashPsiFileUtils.findRelativeFile(containingFile, fileReference.getFilename());
        }
    }
}
