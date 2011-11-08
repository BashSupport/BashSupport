/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: GlocalLocalVarDefInspection.java, Class: GlocalLocalVarDefInspection
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

import com.ansorgit.plugins.bash.editor.inspections.quickfix.RemoveLocalQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import static com.ansorgit.plugins.bash.lang.LanguageBuiltins.localVarDefCommands;

/**
 * This inspection detects local variable declarations on the global level, i.e. outside of functions.
 * <p/>
 * Date: 15.05.2009
 * Time: 14:56:55
 *
 * @author Joachim Ansorg
 */
public class GlocalLocalVarDefInspection extends AbstractBashInspection {

    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "GlobalLocalVarDef";
    }

    @NotNull
    public String getShortName() {
        return "Global definition of local var";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Global definition of a local variable";
    }

    @Override
    public String getStaticDescription() {
        return "If a local variable has been declared on the global level it is invalid.";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitVarDef(BashVarDef varDef) {
                if (varDef.isFunctionScopeLocal()) {
                    PsiElement context = varDef.getContext();

                    if (context instanceof BashCommand) {
                        final BashCommand parentCmd = (BashCommand) context;

                        if (parentCmd.isVarDefCommand() && localVarDefCommands.contains(parentCmd.getReferencedName())) {
                            boolean isInFunction = false;

                            PsiElement parent = BashPsiUtils.findEnclosingBlock(varDef);
                            while (parent != null && !isInFunction) {
                                isInFunction = parent instanceof BashFunctionDef;

                                parent = BashPsiUtils.findEnclosingBlock(parent);
                            }

                            if (!isInFunction) {
                                holder.registerProblem(varDef.getContext(),
                                        "Local must be used in a function",
                                        ProblemHighlightType.GENERIC_ERROR,
                                        new RemoveLocalQuickfix(varDef));
                            }
                        }
                    }
                }
            }
        };
    }
}