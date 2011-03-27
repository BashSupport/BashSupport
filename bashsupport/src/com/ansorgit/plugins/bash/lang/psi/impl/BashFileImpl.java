/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFileImpl.java, Class: BashFileImpl
 * Last modified: 2010-06-30
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
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * PSI implementation for a Bash file.
 */
public class BashFileImpl extends PsiFileBase implements BashFile {
    private List<PsiFile> includedFiles;

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

    public Set<PsiFile> findIncludedFiles(boolean diveDeep, boolean bashOnly) {
        Set<PsiFile> result = Sets.newLinkedHashSet();

        findIncludedFiles(result, diveDeep, bashOnly);

        return result;
    }

    /**
     * Returns the included files and all included subfiles.
     *
     * @param result   The holder of the found included files
     * @param diveDeep
     * @param bashOnly
     * @return
     */
    private void findIncludedFiles(final Set<PsiFile> result, final boolean diveDeep, final boolean bashOnly) {
        //the unfiltered list of included files is cached
        //because it is expensive to compute the list every time
        if (includedFiles == null) {
            final List<PsiFile> files = Lists.newLinkedList();

            BashPsiUtils.visitRecursively(this, new BashVisitor() {
                @Override
                public void visitIncludeCommand(BashIncludeCommand includeCommand) {
                    PsiFile includedFile = includeCommand.getFileReference().findReferencedFile();

                    files.add(includedFile);
                }
            });

            includedFiles = files;
        }

        for (PsiFile file : includedFiles) {
            if (result.contains(file)) {
                continue;
            }

            if (bashOnly && !(file instanceof BashFile)) {
                continue;
            }

            result.add(file);

            if (diveDeep && file instanceof BashFileImpl) {
                ((BashFileImpl) file).findIncludedFiles(result, true, bashOnly);
            }
        }
    }

    @Override
    public boolean processDeclarations(@NotNull final PsiScopeProcessor processor, @NotNull final ResolveState state, final PsiElement lastParent, @NotNull final PsiElement place) {
        return BashPsiUtils.processChildDeclarations(this, processor, state, lastParent, place);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitFile(this);
        } else {
            visitor.visitFile(this);
        }
    }

    @Override
    public void subtreeChanged() {
        this.includedFiles = null;

        super.subtreeChanged();
    }
}
