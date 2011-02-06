package com.ansorgit.plugins.bash.util;

import com.google.common.base.Function;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

/**
 * User: jansorg
 * Date: 06.02.11
 * Time: 21:01
 */
public class BashFunctions {
    public static Function<? super PsiFile, VirtualFile> psiToVirtualFile() {
        return new Function<PsiFile, VirtualFile>() {
            public VirtualFile apply(PsiFile psiFile) {
                return psiFile.getVirtualFile();
            }
        };
    }
}
