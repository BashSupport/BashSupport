/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFileStubBuilder.java, Class: BashFileStubBuilder
 * Last modified: 2012-12-11
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

package com.ansorgit.plugins.bash.lang.psi.stubs;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.stubs.impl.BashFileStubImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

public class BashFileStubBuilder extends DefaultStubBuilder {
    @Override
    public StubElement buildStubTree(@NotNull PsiFile file) {
        return super.buildStubTree(file);
    }

    @NotNull
    protected StubElement createStubForFile(@NotNull final PsiFile file) {
        if (file instanceof BashFile) {
            return new BashFileStubImpl((BashFile) file);
        }

        return super.createStubForFile(file);
    }

    @Override
    protected boolean skipChildProcessingWhenBuildingStubs(@NotNull PsiElement parent, @NotNull PsiElement element) {
        return super.skipChildProcessingWhenBuildingStubs(parent, element);
    }

    @Override
    public boolean skipChildProcessingWhenBuildingStubs(@NotNull ASTNode parent, @NotNull ASTNode node) {
        return super.skipChildProcessingWhenBuildingStubs(parent, node);
    }
}
