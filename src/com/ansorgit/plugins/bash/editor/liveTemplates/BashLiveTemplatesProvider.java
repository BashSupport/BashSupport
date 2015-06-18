package com.ansorgit.plugins.bash.editor.liveTemplates;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Live templates for the Bash language.
 */
public class BashLiveTemplatesProvider implements DefaultLiveTemplatesProvider {

    private static final String[] EMPTY = new String[0];

    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return new String[]{"/liveTemplates/Bash"};
    }

    @Nullable
    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return EMPTY;
    }
}
