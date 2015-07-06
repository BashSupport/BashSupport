package com.ansorgit.plugins.bash.lang;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.FileViewProviderFactory;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

public class BashViewProviderFactory implements FileViewProviderFactory {
    @NotNull
    @Override
    public FileViewProvider createFileViewProvider(@NotNull VirtualFile file, Language language, PsiManager manager, boolean eventSystemEnabled) {
        return new BashMultipleViewProvider(manager, file, eventSystemEnabled);
    }

    private static class BashMultipleViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider {
        public BashMultipleViewProvider(PsiManager manager, VirtualFile file, boolean eventSystemEnabled) {
            super(manager, file, eventSystemEnabled);
        }

        @NotNull
        @Override
        public Language getBaseLanguage() {
            return BashFileType.BASH_LANGUAGE;
        }

        @Override
        protected MultiplePsiFilesPerDocumentFileViewProvider cloneInner(VirtualFile fileCopy) {
            return new BashMultipleViewProvider(getManager(), fileCopy, isEventSystemEnabled());
        }
    }
}
