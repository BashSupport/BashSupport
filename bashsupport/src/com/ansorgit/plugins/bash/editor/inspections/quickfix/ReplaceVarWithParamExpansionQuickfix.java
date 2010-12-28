package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ReadOnlyFragmentModificationException;
import com.intellij.openapi.editor.ReadOnlyModificationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * THis quickfix replaces a simple variable usage with the equivalent parameter expansion form.
 * User: jansorg
 * Date: 28.12.10
 * Time: 12:19
 */
public class ReplaceVarWithParamExpansionQuickfix extends AbstractBashQuickfix implements LocalQuickFix {
    private final BashVar var;
    protected final String variableName;

    public ReplaceVarWithParamExpansionQuickfix(BashVar var) {
        this.var = var;
        variableName = var.getReferencedName();
    }

    @NotNull
    public String getName() {
        if (variableName.length() > 10) {
            return "Replace with '${...}'";
        } else {
            return String.format("Replace '%s' with '${%s}'", variableName, variableName);
        }
    }

    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        TextRange textRange = var.getTextRange();

        //replace this position with the same value, we have to trigger a reparse somehow
        try {
            file.getViewProvider().getDocument().replaceString(textRange.getStartOffset(), textRange.getEndOffset(), "${" + variableName + "}");
        } catch (ReadOnlyModificationException e) {
            //ignore
        } catch (ReadOnlyFragmentModificationException e) {
            //ignore
        }

        file.subtreeChanged();
    }
}
