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

import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashCommandStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author jansorg
 */
public class BashSimpleCommandImpl extends AbstractBashCommand<BashCommandStub> implements StubBasedPsiElement<BashCommandStub> {

    public BashSimpleCommandImpl(ASTNode astNode) {
        super(astNode, "Simple command");
    }

    public BashSimpleCommandImpl(@NotNull BashCommandStub stub, @NotNull IStubElementType nodeType, @Nullable String name) {
        super(stub, nodeType, name);
    }

}
