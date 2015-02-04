/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: UnknownFiledescriptorInspection.java, Class: UnknownFiledescriptorInspection
 * Last modified: 2011-04-30 16:33
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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashFiledescriptor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Detects invalid filedescriptors. Bash only supports the descriptors 0-9.
 *
 * @author Joachim Ansorg
 */
public class UnknownFiledescriptorInspection extends LocalInspectionTool {
    public UnknownFiledescriptorInspection() {
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitFiledescriptor(BashFiledescriptor descriptor) {
                Integer asInt = descriptor.descriptorAsInt();
                if (asInt != null) {
                    if (asInt < 0 || asInt > 9) {
                        holder.registerProblem(descriptor, BashPsiUtils.rangeInParent(descriptor, descriptor),
                                "Invalid file descriptor " + asInt);
                    }
                }
            }
        };
    }
}