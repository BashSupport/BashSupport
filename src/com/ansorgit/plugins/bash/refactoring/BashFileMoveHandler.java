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

package com.ansorgit.plugins.bash.refactoring;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Maps;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFileHandler;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Handles move refactorings of Bash files. It changes references pointing to relative files to point to the new relative location.
 *
 * @author jansorg
 */
public class BashFileMoveHandler extends MoveFileHandler {
    private static final Key<Map<PsiReference, PsiFileSystemItem>> REPLACEMENT_MAP = new Key<Map<PsiReference, PsiFileSystemItem>>("move file replacements");

    @Override
    public boolean canProcessElement(PsiFile element) {
        return element instanceof BashFile;
    }

    @Override
    public void prepareMovedFile(final PsiFile file, final PsiDirectory moveDestination, final Map<PsiElement, PsiElement> oldToNewMap) {
        FileReferenceCollectionVisitor visitor = new FileReferenceCollectionVisitor();

        BashPsiUtils.visitRecursively(file, visitor);

        REPLACEMENT_MAP.set(file, visitor.getReferenceMap());
    }

    @Nullable
    @Override
    public List<UsageInfo> findUsages(PsiFile psiFile, PsiDirectory newParent, boolean searchInComments, boolean searchInNonJavaFiles) {
        return null;
    }

    @Override
    public void retargetUsages(List<UsageInfo> usageInfos, Map<PsiElement, PsiElement> oldToNewMap) {
    }

    /**
     * Updates all Bash commands and Bash include commands to point to the new relative path.
     *
     * @param file The file which is already moved to the new location
     * @throws IncorrectOperationException
     */
    @Override
    public void updateMovedFile(PsiFile file) throws IncorrectOperationException {
        try {
            Map<PsiReference, PsiFileSystemItem> psiReferences = REPLACEMENT_MAP.get(file);

            if (psiReferences != null) {
                for (Map.Entry<PsiReference, PsiFileSystemItem> entry : psiReferences.entrySet()) {
                    PsiReference key = entry.getKey();
                    if (key instanceof BindablePsiReference) {
                        key.bindToElement(entry.getValue());
                    }
                }

                psiReferences.clear();
            }
        } finally {
            REPLACEMENT_MAP.set(file, null);
        }
    }

    private static class FileReferenceCollectionVisitor extends BashVisitor {
        private final Map<PsiReference, PsiFileSystemItem> replacementMap;

        public FileReferenceCollectionVisitor() {
            this.replacementMap = Maps.newLinkedHashMap();
        }

        public Map<PsiReference, PsiFileSystemItem> getReferenceMap() {
            return this.replacementMap;
        }

        @Override
        public void visitFileReference(BashFileReference fileReference) {
            handleReference(fileReference.getReference());
        }

        @Override
        public void visitGenericCommand(BashCommand bashCommand) {
            if (bashCommand.isBashScriptCall()) {
                handleReference(bashCommand.getReference());
            }
        }

        private void handleReference(@Nullable PsiReference psiReference) {
            if (psiReference != null) {
                PsiElement oldTarget = psiReference.resolve();

                if (oldTarget instanceof PsiFileSystemItem) {
                    replacementMap.put(psiReference, (PsiFileSystemItem) oldTarget);
                }
            }
        }
    }
}
