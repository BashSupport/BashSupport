package com.ansorgit.plugins.bash.editor.refactoring;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.MultiFileTestCase;
import com.intellij.refactoring.rename.RenameProcessor;
import com.intellij.refactoring.rename.naming.AutomaticRenamerFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

/**
 */
public class BashFileReferenceTest extends MultiFileTestCase {
    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath();
    }

    @NotNull
    @Override
    protected String getTestRoot() {
        return "/editor/refactoring/RenameFileReferenceTest/";
    }

    @Override
    protected boolean isRunInWriteAction() {
        return true;
    }

    /**
     * renames a file reference and checks that the referenced file is renamed as well.
     *
     * @throws Exception
     */
    @Test
    public void testRenameFileReference() throws Exception {
        doTest(new PerformAction() {
            @Override
            public void performAction(final VirtualFile rootDir, VirtualFile rootAfter) throws Exception {
                PsiFile sourceFile = myPsiManager.findFile(rootDir.findChild("source.bash"));

                PsiElement includeCommand = PsiTreeUtil.getParentOfType(sourceFile.findElementAt(0), BashIncludeCommand.class);
                assert includeCommand != null;

                BashFileReference targetReference = ((BashIncludeCommand) includeCommand).getFileReference();
                assert targetReference != null;

                doRename(targetReference.findReferencedFile(), "target_renamed.bash");
            }
        });
    }

    /**
     * renames a file and checks that the reference to the target file is changed, too.
     *
     * @throws Exception
     */
    @Test
    public void testRenameTargetFile() throws Exception {
        doTest(new PerformAction() {
            @Override
            public void performAction(final VirtualFile rootDir, VirtualFile rootAfter) throws Exception {
                PsiFile targetFile = myPsiManager.findFile(rootDir.findChild("target.bash"));

                doRename(targetFile, "target_renamed.bash");
            }
        });
    }

    private void doRename(PsiFile psiElement, String newName) {
        final RenameProcessor processor = new RenameProcessor(myProject, psiElement, newName, false, false);
        for (AutomaticRenamerFactory factory : Extensions.getExtensions(AutomaticRenamerFactory.EP_NAME)) {
            processor.addRenamerFactory(factory);
        }
        processor.run();

        PsiDocumentManager.getInstance(myProject).commitAllDocuments();
        FileDocumentManager.getInstance().saveAllDocuments();
    }
}
