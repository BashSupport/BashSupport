package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class FunctionNameVisitor extends BashVisitor {
    private static final Pattern LOWER_SNAKE_CASE = Pattern.compile("([a-z_0-9]|::)*");
    private final ProblemsHolder holder;

    public FunctionNameVisitor(ProblemsHolder holder) {
        this.holder = holder;
    }

    @Override
    public void visitFunctionDef(BashFunctionDef functionDef) {
        String functionName = functionDef.getName();
        if (functionName != null && doesNotMatchPattern(functionName)) {
            PsiElement targetElement = functionDef.getNameSymbol();
            if (targetElement == null) {
                targetElement = functionDef.getNavigationElement();
            }
            holder.registerProblem(targetElement, "Function name must fit lower snake case", ProblemHighlightType.WEAK_WARNING);
        }
    }

    private static boolean doesNotMatchPattern(@NotNull CharSequence functionName) {
        return !LOWER_SNAKE_CASE.matcher(functionName).matches();
    }

}
