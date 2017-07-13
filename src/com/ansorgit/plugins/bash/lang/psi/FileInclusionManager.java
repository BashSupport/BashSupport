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

package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludeCommandIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludedFilenamesIndex;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashSearchScopes;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author jansorg
 */
public class FileInclusionManager {
    private FileInclusionManager() {
    }

    @NotNull
    public static Set<PsiFile> findIncludedFiles(@NotNull PsiFile sourceFile, boolean diveDeep, boolean bashOnly) {
        if (!(sourceFile instanceof BashFile)) {
            return Collections.emptySet();
        }

        if (!sourceFile.isPhysical()) {
            return Collections.emptySet();
        }

        if (DumbService.isDumb(sourceFile.getProject())) {
            return Collections.emptySet();
        }

        Project project = sourceFile.getProject();

        Set<PsiFile> includersTodo = Sets.newLinkedHashSet(Collections.singletonList(sourceFile));
        Set<PsiFile> includersDone = Sets.newLinkedHashSet();

        Set<PsiFile> allIncludedFiles = Sets.newHashSet();

        while (!includersTodo.isEmpty()) {
            Iterator<PsiFile> iterator = includersTodo.iterator();

            PsiFile file = iterator.next();
            iterator.remove();

            includersDone.add(file);

            VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null) {
                continue;
            }

            String filePath = virtualFile.getPath();

            Collection<BashIncludeCommand> commands = StubIndex.getElements(BashIncludeCommandIndex.KEY, filePath, project, BashSearchScopes.bashOnly(BashSearchScopes.moduleScope(file)), BashIncludeCommand.class);
            if (commands.isEmpty()) {
                continue;
            }

            for (BashIncludeCommand command : commands) {
                BashFileReference fileReference = command.getFileReference();
                if (fileReference != null && fileReference.isStatic()) {
                    PsiFile referencedFile = fileReference.findReferencedFile();
                    if (bashOnly && !(referencedFile instanceof BashFile)) {
                        continue;
                    }

                    if (referencedFile != null) {
                        allIncludedFiles.add(referencedFile);

                        if (!includersDone.contains(referencedFile)) {
                            //the include commands of this command have to be collected, too
                            includersTodo.add(referencedFile);
                        }
                    }
                }
            }

            if (!diveDeep) {
                //the first iteration is the original source
                break;
            }
        }

        return allIncludedFiles;
    }

    /**
     * Finds all files which include the given file.
     * The bash files of the module are checked if they include the file.
     *
     * @param project The project
     * @param file    The file for which the includers should be found.
     * @return
     */
    @NotNull
    public static Set<BashFile> findIncluders(@NotNull Project project, @NotNull PsiFile file) {
        if (DumbService.isDumb(project)) {
            return Collections.emptySet();
        }

        GlobalSearchScope searchScope = BashSearchScopes.moduleScope(file);

        String filename = file.getName();
        if (StringUtils.isEmpty(filename)) {
            return Collections.emptySet();
        }

        Collection<BashIncludeCommand> includeCommands = StubIndex.getElements(BashIncludedFilenamesIndex.KEY, filename, project, BashSearchScopes.bashOnly(searchScope), BashIncludeCommand.class);
        if (includeCommands == null || includeCommands.isEmpty()) {
            return Collections.emptySet();
        }

        Set<BashFile> includers = Sets.newLinkedHashSet();
        for (BashIncludeCommand command : includeCommands) {
            BashFile includer = (BashFile) BashPsiUtils.findFileContext(command);

            if (!file.equals(includer)) {
                includers.add(includer);
            }
        }

        return includers;
    }

    public static GlobalSearchScope includedFilesUnionScope(PsiFile source) {
        List<VirtualFile> scopes = Lists.newLinkedList();
        scopes.add(source.getVirtualFile());

        Set<PsiFile> includedFiles = findIncludedFiles(source, true, true);
        for (PsiFile file : includedFiles) {
            scopes.add(file.getVirtualFile());
        }

        return GlobalSearchScope.filesScope(source.getProject(), scopes);
    }
}
