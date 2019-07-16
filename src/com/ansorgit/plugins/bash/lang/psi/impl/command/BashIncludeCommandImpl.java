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

package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.jetbrains.PsiScopesUtil;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashIncludeCommandStub;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author jansorg
 */
public class BashIncludeCommandImpl extends AbstractBashCommand<BashIncludeCommandStub> implements BashIncludeCommand, StubBasedPsiElement<BashIncludeCommandStub> {
    public BashIncludeCommandImpl(ASTNode astNode) {
        super(astNode, "Bash include command");
    }

    public BashIncludeCommandImpl(@NotNull BashIncludeCommandStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType, null);
    }

    @Nullable
    public BashFileReference getFileReference() {
        return findChildByClass(BashFileReference.class);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitIncludeCommand(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public boolean isIncludeCommand() {
        return true;
    }

    @Override
    public boolean isFunctionCall() {
        return false;
    }

    @Override
    public boolean isInternalCommand() {
        return true;
    }

    @Override
    public boolean isExternalCommand() {
        return false;
    }

    @Override
    public boolean isPureAssignment() {
        return false;
    }

    @Override
    public boolean isVarDefCommand() {
        return false;
    }

    @Override
    public boolean isBashScriptCall() {
        return false;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        boolean result = PsiScopesUtil.walkChildrenScopes(this, processor, state, lastParent, place);
        if (!result) {
            //processing is done here
            return false;
        }

        //fixme right file?
        PsiFile containingFile = getContainingFile();
        PsiFile includedFile = BashPsiUtils.findIncludedFile(this);

        Multimap<VirtualFile, PsiElement> visitedFiles = state.get(visitedIncludeFiles);
        if (visitedFiles == null) {
            visitedFiles = Multimaps.newListMultimap(Maps.newHashMap(), Lists::newLinkedList);
        }

        visitedFiles.put(containingFile.getVirtualFile(), null);

        if (includedFile != null && !visitedFiles.containsKey(includedFile.getVirtualFile())) {
            //mark the file as visited before the actual visit, otherwise we'll get a stack overflow
            visitedFiles.put(includedFile.getVirtualFile(), this);

            state = state.put(visitedIncludeFiles, visitedFiles);

            return includedFile.processDeclarations(processor, state, null, place);
        }

        return true;
    }
}
