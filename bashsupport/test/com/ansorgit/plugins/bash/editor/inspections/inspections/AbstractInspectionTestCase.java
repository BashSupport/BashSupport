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
import com.intellij.codeInspection.*;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.testFramework.InspectionTestCase;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;

public abstract class AbstractInspectionTestCase extends InspectionTestCase {
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/inspection";
    }

    @Override
    public void doTest(@NonNls String folderName, LocalInspectionTool tool) {
        //fixme
        doTest(folderName, tool.getClass(), false);
    }

    /**
     * This method looks at the LocalInspectionTool extension points registered in BashSupport's plugin.xml.
     * The base InspectionTestCase doesn't handle inspections well which are only registered in the plugin.xml and
     * which do not have a seperately overridden displayName implementation.
     *
     * @param folderName
     * @param clazz
     * @param onTheFly
     */
    public void doTest(String folderName, Class<? extends LocalInspectionTool> clazz, boolean onTheFly) {
        LocalInspectionEP[] extensions = Extensions.getExtensions(LocalInspectionEP.LOCAL_INSPECTION);
        for (LocalInspectionEP extension : extensions) {
            if (extension.implementationClass.equals(clazz.getCanonicalName())) {
                extension.enabledByDefault = true;

                InspectionProfileEntry instance = extension.instantiateTool();

                super.doTest(folderName, onTheFly ? withOnTheFly((LocalInspectionTool) instance) : (LocalInspectionTool) instance);

                return;
            }
        }

        Assert.fail("Inspection not found for type " + clazz.getName());
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
        public void inspectionStarted(@NotNull LocalInspectionToolSession session, boolean isOnTheFly) {
            delegate.inspectionStarted(session, isOnTheFly);
        }

        @Override
        public void inspectionFinished(@NotNull LocalInspectionToolSession session, @NotNull ProblemsHolder problemsHolder) {
            delegate.inspectionFinished(session, problemsHolder);
        }

        @Override
        @Deprecated
        public void inspectionFinished(@NotNull LocalInspectionToolSession session) {
            delegate.inspectionFinished(session);
        }

        @Pattern("[a-zA-Z_0-9.-]+")
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
        public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
            return delegate.buildVisitor(holder, true, session);
        }
    }
}
