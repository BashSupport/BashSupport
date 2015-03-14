package com.ansorgit.plugins.bash.editor.inspections;

import com.ansorgit.plugins.bash.editor.inspections.inspections.AddShebangInspection;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class to work with Bash suppression comments.
 *
 * @author jansorg
 */
public class SupressionUtil {
    private static final String SUPPRESSION_PREFIX = "@IgnoreInspection";

    @Nullable
    public static PsiComment findSuppressionComment(PsiElement anchor) {
        return PsiTreeUtil.getChildOfType(anchor, PsiComment.class);
    }

    public static boolean isSuppressionComment(PsiComment suppressionComment, String inspectionId) {
        return suppressionComment != null && suppressionComment.getText().trim().endsWith(SUPPRESSION_PREFIX + " " + inspectionId);
    }

    public static PsiComment createSuppressionComment(Project project, String id) {
        return BashChangeUtil.createComment(project, SUPPRESSION_PREFIX + " " + id);
    }
}
