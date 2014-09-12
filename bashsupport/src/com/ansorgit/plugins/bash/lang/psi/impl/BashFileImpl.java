/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFileImpl.java, Class: BashFileImpl
 * Last modified: 2013-05-12
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

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.apache.commons.lang.BooleanUtils;
import org.jetbrains.annotations.NotNull;

/**
 * PSI implementation for a Bash file.
 */
public class BashFileImpl extends PsiFileBase implements BashFile {
    public BashFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, BashFileType.BASH_LANGUAGE);
    }

    @NotNull
    public FileType getFileType() {
        return BashFileType.BASH_FILE_TYPE;
    }

    public boolean hasShebangLine() {
        return findChildByClass(BashShebang.class) != null;
    }

    public BashFunctionDef[] functionDefinitions() {
        return findChildrenByClass(BashFunctionDef.class);
    }

    @Override
    public boolean processDeclarations(@NotNull final PsiScopeProcessor processor, @NotNull final ResolveState state, final PsiElement lastParent, @NotNull final PsiElement place) {
        if (!processor.execute(this, state)) {
            return false;
        }

        boolean walkDeep = BooleanUtils.toBooleanDefaultIfNull(processor.getHint(Keys.FILE_WALK_GO_DEEP), true);
        boolean moreProcessing = true;
        if (walkDeep) {
            PsiElement child = getFirstChild();

            while (child != null) {
                if (child != lastParent && !child.processDeclarations(processor, state, lastParent, place)) {
                    moreProcessing = false;
                }

                child = child.getNextSibling();
            }
        } else {
            //walk the toplevel psi elements without diving into them
            //we can compute the first element to walk a bit smarter than getFirstChild().
            //It's the next toplevel element after place, i.e. starting element

            PsiElement child = getFirstChild();
            while (child != null) {
                if (!processor.execute(child, state)) {
                    moreProcessing = false;
                    break;
                }

                //include commands have to be visited, though
                if (child instanceof BashIncludeCommand) {
                    if (!child.processDeclarations(processor, state, lastParent, place)) {
                        moreProcessing = false;
                    }
                }

                child = child.getNextSibling();
            }
        }

        return moreProcessing;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitFile(this);
        } else {
            visitor.visitFile(this);
        }
    }
}
