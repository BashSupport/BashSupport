package com.ansorgit.plugins.bash.editor.liveTemplates;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a live template context for Bash files.
 */
public class BashLiveTemplatesContext extends TemplateContextType {
    protected BashLiveTemplatesContext() {
        super("Bash", "Bash");
    }

    @Override
    public boolean isInContext(@NotNull PsiFile file, int offset) {
        return file instanceof BashFile;
    }
}
