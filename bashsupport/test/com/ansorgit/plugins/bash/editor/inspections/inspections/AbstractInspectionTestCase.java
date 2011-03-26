/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractInspectionTestCase.java, Class: AbstractInspectionTestCase
 * Last modified: 2010-07-01
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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.editor.inspections.InspectionProvider;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.testFramework.InspectionTestCase;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * User: jansorg
 * Date: 01.07.2010
 * Time: 18:48:20
 */
public abstract class AbstractInspectionTestCase extends InspectionTestCase {
    protected AbstractInspectionTestCase(Class<?> inspectionClass) {
        if (!Arrays.asList(new InspectionProvider().getInspectionClasses()).contains(inspectionClass)) {
            throw new IllegalStateException("The inspection is not registered in the inspection provider");
        }
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/inspection/";
    }

    protected LocalInspectionTool withOnTheFly(final LocalInspectionTool delegate) {
        return new MyLocalInspectionTool(delegate);
    }

    private static class MyLocalInspectionTool extends LocalInspectionTool {
        private final LocalInspectionTool delegate;

        public MyLocalInspectionTool(LocalInspectionTool delegate) {
            this.delegate = delegate;
        }

        @Nls
        @NotNull
        @Override
        public String getGroupDisplayName() {
            return delegate.getGroupDisplayName();
        }

        @Override
        @NotNull
        public String[] getGroupPath() {
            return delegate.getGroupPath();
        }

        @Nls
        @NotNull
        @Override
        public String getDisplayName() {
            return delegate.getDisplayName();
        }

        @NotNull
        @Override
        public String getShortName() {
            return delegate.getShortName();
        }

        @NotNull
        @Override
        public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
            return delegate.buildVisitor(holder, true);
        }

        @Override
        @Nullable
        public PsiNamedElement getProblemElement(PsiElement psiElement) {
            return delegate.getProblemElement(psiElement);
        }

        @Override
        public void inspectionStarted(LocalInspectionToolSession session) {
            delegate.inspectionStarted(session);
        }

        @Override
        public void inspectionFinished(LocalInspectionToolSession session, ProblemsHolder problemsHolder) {
            delegate.inspectionFinished(session, problemsHolder);
        }

        @Override
        @Deprecated
        public void inspectionFinished(LocalInspectionToolSession session) {
            delegate.inspectionFinished(session);
        }

        @Override
        @NotNull
        @NonNls
        public String getID() {
            return delegate.getID();
        }

        @Override
        @Nullable
        @NonNls
        public String getAlternativeID() {
            return delegate.getAlternativeID();
        }

        @Override
        public boolean runForWholeFile() {
            return delegate.runForWholeFile();
        }

        @Override
        @Nullable
        public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
            return delegate.checkFile(file, manager, isOnTheFly);
        }

        @Override
        @NotNull
        public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, LocalInspectionToolSession session) {
            return delegate.buildVisitor(holder, true, session);
        }
    }
}
