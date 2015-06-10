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
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BashFileReferenceImpl extends BashBaseStubElementImpl<StubElement> implements BashFileReference {
    private FileReferenceSet fileReferenceSet;

    public BashFileReferenceImpl(final ASTNode astNode) {
        super(astNode, "Bash File reference");
        this.fileReferenceSet = new FileReferenceSet(this);
    }

    @Nullable
    @Override
    public PsiFile findReferencedFile() {
        return (PsiFile) fileReferenceSet.resolve();
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
        return fileReferenceSet.getLastReference();
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
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        FileReference lastReference = fileReferenceSet.getLastReference();
        if (lastReference == null) {
            throw new IncorrectOperationException("no reference found");
        }

        return lastReference.handleElementRename(name);
    }
}
