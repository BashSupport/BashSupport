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

import com.ansorgit.plugins.bash.lang.psi.FileInclusionManager;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashFunctionNameIndex;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Reference to functions defined in Bash files.
 * This implementation handles dumb mode operations. Index use is turned off during dumb mode operations.
 *
 * @author jansorg
 */
class SmartFunctionReference extends AbstractFunctionReference {

    public SmartFunctionReference(AbstractBashCommand<?> cmd) {
        super(cmd);
    }

    @Nullable
    @Override
    public PsiElement resolveInner() {
        final String referencedName = cmd.getReferencedCommandName();
        if (referencedName == null) {
            return null;
        }

        final ResolveProcessor processor = new BashFunctionProcessor(referencedName);

        Project project = cmd.getProject();
        PsiFile currentFile = cmd.getContainingFile();

        GlobalSearchScope allFiles = FileInclusionManager.includedFilesUnionScope(currentFile);
        Collection<BashFunctionDef> functionDefs = StubIndex.getElements(BashFunctionNameIndex.KEY, referencedName, project, allFiles, BashFunctionDef.class);

        ResolveState initial = ResolveState.initial();
        for (BashFunctionDef functionDef : functionDefs) {
            processor.execute(functionDef, initial);
        }

        //find include commands which are relevant for the start element
        if (!processor.hasResults()) {
            Set<BashFile> includingFiles = FileInclusionManager.findIncluders(project, currentFile);

            List<GlobalSearchScope> scopes = Lists.newLinkedList();
            for (BashFile file : includingFiles) {
                scopes.add(GlobalSearchScope.fileScope(file));
            }

            if (!scopes.isEmpty()) {
                GlobalSearchScope scope = GlobalSearchScope.union(scopes.toArray(new GlobalSearchScope[scopes.size()]));

                functionDefs = StubIndex.getElements(BashFunctionNameIndex.KEY, referencedName, project, scope, BashFunctionDef.class);

                for (BashFunctionDef def : functionDefs) {
                    processor.execute(def, initial);
                }
            }
        }

        processor.prepareResults();

        return processor.hasResults() ? processor.getBestResult(true, cmd) : null;
    }

}
