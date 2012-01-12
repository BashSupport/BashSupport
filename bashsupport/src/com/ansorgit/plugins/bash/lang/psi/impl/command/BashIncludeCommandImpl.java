/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashIncludeCommandImpl.java, Class: BashIncludeCommandImpl
 * Last modified: 2011-03-27 15:07
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashFunctionDefStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashIncludeCommandStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: jansorg
 * Date: 18.02.11
 * Time: 20:17
 */
public class BashIncludeCommandImpl extends BashCommandImpl<BashIncludeCommandStub> implements BashIncludeCommand, StubBasedPsiElement<BashIncludeCommandStub> {
    public BashIncludeCommandImpl(ASTNode astNode) {
        super(astNode, "Bash include command");
    }

    public BashIncludeCommandImpl(@NotNull BashIncludeCommandStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType, null);
    }

    @NotNull
    public BashFileReference getFileReference() {
        return findNotNullChildByClass(BashFileReference.class);
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
    public boolean canNavigate() {
        return canNavigateToSource();
    }

    @Override
    public boolean canNavigateToSource() {
        return getFileReference().findReferencedFile() != null;
    }
}
