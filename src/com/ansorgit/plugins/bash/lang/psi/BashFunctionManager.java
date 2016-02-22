package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author jansorg
 */
public class BashFunctionManager {
    /**
     * Returns all function definitions found in the given files
     *
     * @param project
     * @return
     */
    @NotNull
    public static List<BashFunctionDef> findFunctionDefintions(Project project, List<PsiFile> files) {
        if (DumbService.isDumb(project)) {
            return findFunctionDefintionsDumb(project, files);
        }

        return findFunctionDefintionsSmart(project, files);
    }

    private static List<BashFunctionDef> findFunctionDefintionsSmart(Project project, List<PsiFile> files) {
        return null;
    }

    private static List<BashFunctionDef> findFunctionDefintionsDumb(Project project, List<PsiFile> files) {
        return null;
    }


}
