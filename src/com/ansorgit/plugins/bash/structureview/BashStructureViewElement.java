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

package com.ansorgit.plugins.bash.structureview;

import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jansorg
 */
class BashStructureViewElement implements StructureViewTreeElement {
    private final PsiElement myElement;

    BashStructureViewElement(PsiElement element) {
        this.myElement = element;
    }

    public PsiElement getValue() {
        return myElement;
    }

    @NotNull
    public ItemPresentation getPresentation() {
        if (myElement instanceof NavigationItem) {
            ItemPresentation presentation = ((NavigationItem) myElement).getPresentation();
            if (presentation != null) {
                return presentation;
            }
        }

        //fallback
        return new BashItemPresentation();
    }

    @NotNull
    public TreeElement[] getChildren() {
        final List<BashPsiElement> childrenElements = new ArrayList<BashPsiElement>();
        myElement.acceptChildren(new PsiElementVisitor() {
            public void visitElement(PsiElement element) {
                if (isBrowsableElement(element)) {
                    childrenElements.add((BashPsiElement) element);
                } else {
                    element.acceptChildren(this);
                }
            }
        });

        StructureViewTreeElement[] children = new StructureViewTreeElement[childrenElements.size()];
        for (int i = 0; i < children.length; i++) {
            children[i] = new BashStructureViewElement(childrenElements.get(i));
        }

        return children;
    }

    private boolean isBrowsableElement(PsiElement element) {
        return (element instanceof BashFunctionDef) && (((BashFunctionDef) element).getNameSymbol() != null);
    }

    public void navigate(boolean requestFocus) {
        ((NavigationItem) myElement).navigate(requestFocus);
    }

    public boolean canNavigate() {
        return ((NavigationItem) myElement).canNavigateToSource();
    }

    public boolean canNavigateToSource() {
        return ((NavigationItem) myElement).canNavigateToSource();
    }

    private class BashItemPresentation implements ItemPresentation {
        public String getPresentableText() {
            return ((PsiNamedElement) myElement).getName();
        }

        public String getLocationString() {
            return null;
        }

        public Icon getIcon(boolean open) {
            return null;
        }
    }
}
