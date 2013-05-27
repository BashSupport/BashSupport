package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;

public class IBashElementType extends IElementType {
    private final boolean myLeftBound;

    public IBashElementType(@NonNls final String debugName) {
        this(debugName, false);
    }

    public IBashElementType(@NonNls final String debugName, final boolean leftBound) {
        super(debugName, BashFileType.BASH_LANGUAGE);
        myLeftBound = leftBound;
    }

    @Override
    public boolean isLeftBound() {
        return myLeftBound;
    }
}