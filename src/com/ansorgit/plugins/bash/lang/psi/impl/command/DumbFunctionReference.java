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

import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Function reference to be used in dumb mode and in scratch files. It resolves without index access.
 *
 * @author jansorg
 */
class DumbFunctionReference extends AbstractFunctionReference {
    public DumbFunctionReference(AbstractBashCommand<?> cmd) {
        super(cmd);
    }

    @Nullable
    @Override
    public PsiElement resolveInner() {
        final String referencedName = cmd.getReferencedCommandName();
        if (referencedName == null) {
            return null;
        }


        // in dumb mode the current is the only one searched for function definitions
        List<BashFunctionDef> functionDefs = cmd.getContainingFile().allFunctionDefinitions();

        ResolveState initial = ResolveState.initial();

        ResolveProcessor processor = new BashFunctionProcessor(referencedName);
        for (BashFunctionDef functionDef : functionDefs) {
            processor.execute(functionDef, initial);
        }

        processor.prepareResults();

        return processor.hasResults() ? processor.getBestResult(true, cmd) : null;
    }
}
