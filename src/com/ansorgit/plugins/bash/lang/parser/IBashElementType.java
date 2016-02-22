package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;

public class IBashElementType extends IElementType {
    public IBashElementType(@NonNls final String debugName) {
        super(debugName, BashFileType.BASH_LANGUAGE);
    }
}