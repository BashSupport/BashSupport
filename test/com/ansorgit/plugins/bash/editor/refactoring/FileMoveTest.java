package com.ansorgit.plugins.bash.editor.refactoring;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.google.common.collect.Sets;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesProcessor;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

/**
 * This test checks renaming of files and the related issues.
 *
 * @author jansorg
 */
public class FileMoveTest extends BashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/editor/refactoring/FileMoveTestCase/";
    }

    /**
     * Tests the basic rename feature for references pointing to files.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void _testBasicFileRename() throws Exception {
        doMove(new String[]{"source.bash", "subdir/source2.bash"}, "target.bash", "subdir");

        PsiReference targetBashRef = myFixture.getReferenceAtCaretPositionWithAssertion("source.bash");

        Assert.assertNotNull("target file reference wasn't found", targetBashRef);
        String canonicalText = targetBashRef.getCanonicalText();
        Assert.assertTrue("Renamed reference wasn't found in the canonical text: " + canonicalText, canonicalText.contains("subdir/target.bash"));

        PsiElement targetFile = targetBashRef.resolve();
        Assert.assertNotNull("target file resolve result wasn't found", targetFile);
        Assert.assertTrue("target is not a psi file", targetFile instanceof BashFile);

        PsiElement parent = targetFile.getParent();
        Assert.assertTrue("target file is not in the target dir", parent instanceof PsiDirectory);
        Assert.assertEquals("target file is not in the target dir named subdir ", "subdir", ((PsiDirectory) parent).getName());
    }

    /**
     * Moves a file which contains references to other files. References in a moved file should change to the releative path pointing to the new target.
     * @throws Exception
     */
    public void testMoveFileWithRefs() throws Exception {
        doMove(new String[]{"source2.bash", "subdir/target.bash"}, "source.bash", "subdir");
    }

    private void doMove(String[] checkedFiles, String movedFileSource, String moveTargetDir) {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));

        Set<String> filenames = Sets.newHashSet(checkedFiles);
        filenames.add(movedFileSource);

        myFixture.configureByFiles(filenames.toArray(new String[filenames.size()]));

        VirtualFile sourceVirtualFile = myFixture.findFileInTempDir(movedFileSource);
        Assert.assertNotNull(sourceVirtualFile);
        PsiFile sourceFile = myFixture.getPsiManager().findFile(sourceVirtualFile);
        moveFile(moveTargetDir, sourceFile);

        for (String filename : checkedFiles) {
            myFixture.checkResultByFile(filename, FileUtil.getNameWithoutExtension(filename) + "_after." + FileUtilRt.getExtension(filename), false);
        }

        myFixture.checkResultByFile(moveTargetDir + "/" + movedFileSource, moveTargetDir + "/" + FileUtil.getNameWithoutExtension(movedFileSource) + "_after." + FileUtilRt.getExtension(movedFileSource), false);
    }

    private void moveFile(String moveTargetDir, PsiFile sourceFile) {
        final VirtualFile dir = myFixture.findFileInTempDir(moveTargetDir);
        assert dir != null : "Directory " + moveTargetDir + " not found";
        assert dir.isDirectory() : moveTargetDir + " is not a directory";

        final PsiDirectory psiTargetDir = myFixture.getPsiManager().findDirectory(dir);

        new MoveFilesOrDirectoriesProcessor(
                myFixture.getProject(),
                new PsiElement[]{sourceFile},
                psiTargetDir,
                false, false,
                null,
                null
        ).run();

        PsiDocumentManager.getInstance(myFixture.getProject()).commitAllDocuments();
        FileDocumentManager.getInstance().saveAllDocuments();
    }
}
