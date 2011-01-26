package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * Inspects function calls and checks whether the given parameters are actually used in the function definition.
 * <p/>
 * This inspection is not capable to evaluate the control flow, e.g. parameter references in unreachable if
 * statements are still evaluated.
 * <p/>
 * <p/>
 * User: jansorg
 * Date: 28.12.10
 * Time: 12:41
 */
public class UnusedFunctionParameterInspection extends AbstractBashInspection {
    @NotNull
    @Override
    public String getID() {
        return "UnusedFunctionParams";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Unused function parameter";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public String getShortName() {
        return "Unused parameter";
    }

    @Override
    public String getStaticDescription() {
        return "Detects unused function parameter values. " +
                "If the caller passes a parameter value " +
                "which is not used inside of the function then it is highlighted.";
    }

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitGenericCommand(BashCommand bashCommand) {
                if (bashCommand.isFunctionCall()) {
                    BashFunctionDef functionDef = (BashFunctionDef) bashCommand.resolve();
                    if (functionDef != null) {
                        List<BashPsiElement> callerParameters = bashCommand.parameters();
                        List<BashVar> usedParameters = functionDef.findReferencedParameters();

                        Set<String> definedParamNames = Sets.newHashSet(Lists.transform(usedParameters, new Function<BashVar, String>() {
                            public String apply(BashVar var) {
                                return var.getReferencedName();
                            }
                        }));

                        //if the parameter count variable is refernced consider all params as used
                        if (definedParamNames.contains("*")) {
                            return;
                        }

                        for (int i = 0; i < callerParameters.size(); i++) {
                            String paramName = String.valueOf(i + 1);

                            if (!definedParamNames.contains(paramName)) {
                                holder.registerProblem(callerParameters.get(i), getShortName(), LocalQuickFix.EMPTY_ARRAY);
                            }
                        }
                    }
                }

            }
        };
    }

}
