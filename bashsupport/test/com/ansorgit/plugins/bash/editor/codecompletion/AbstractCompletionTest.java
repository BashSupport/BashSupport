package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionService;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.PsiTestCase;
import com.intellij.testFramework.TestDataFile;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * User: jansorg
 * Date: 08.02.11
 * Time: 19:24
 */
abstract class AbstractCompletionTest extends PsiTestCase {
    @NonNls
    private static final String MARKER = "<caret>";

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/editor/codecompletion/";
    }

    public List<LookupElement> findCompletions(CompletionType completionType) throws Exception {
        return findCompletions(completionType, new String[]{});
    }

    public List<LookupElement> findCompletions(CompletionType completionType, String... files) throws Exception {
        Pair<PsiElement, Integer> result = configure(files);
        PsiElement element = result.first;
        int offset = result.second;

        CompletionParameters parameters = new MyCompletionParameters(element, myFile, completionType, offset, 1);

        final List<LookupElement> items = Lists.newLinkedList();
        LookupElement[] lookupElements = CompletionService.getCompletionService().performCompletion(parameters, new Consumer<LookupElement>() {
            public void consume(LookupElement lookupElement) {
                items.add(lookupElement);
            }
        });

        return items;
    }

    protected Pair<PsiElement, Integer> configure() throws Exception {
        return configureByFile(getTestName(false) + ".bash");
    }

    protected Pair<PsiElement, Integer> configure(String... files) throws Exception {
        Pair<PsiElement, Integer> result = configureByFile(getTestName(false) + ".bash");

        for (String file : files) {
            addFile(file);
        }

        return result;
    }

    protected PsiFile addFile(@TestDataFile @NonNls String filePath) throws Exception {
        final String fullPath = getTestDataPath() + filePath;
        final VirtualFile vFile = LocalFileSystem.getInstance().findFileByPath(fullPath.replace(File.separatorChar, '/'));
        assertNotNull("file " + filePath + " not found", vFile);

        String fileText = StringUtil.convertLineSeparators(VfsUtil.loadText(vFile));

        final String fileName = vFile.getName();

        return createFile(myModule, myFile.getVirtualFile().getParent(), fileName, fileText);
    }

    protected Pair<PsiElement, Integer> configureByFile(@NonNls String filePath) throws Exception {
        return configureByFile(filePath, null);
    }

    protected Pair<PsiElement, Integer> configureByFile(@TestDataFile @NonNls String filePath, @Nullable VirtualFile parentDir) throws Exception {
        final String fullPath = getTestDataPath() + filePath;
        final VirtualFile vFile = LocalFileSystem.getInstance().findFileByPath(fullPath.replace(File.separatorChar, '/'));
        assertNotNull("file " + filePath + " not found", vFile);

        String fileText = StringUtil.convertLineSeparators(VfsUtil.loadText(vFile));

        final String fileName = vFile.getName();

        return configureByFileText(fileText, fileName, parentDir);
    }

    protected Pair<PsiElement, Integer> configureByFileText(String fileText, String fileName, @Nullable final VirtualFile parentDir) throws Exception {
        int offset = fileText.indexOf(MARKER);
        assertTrue("The marker could not be found in the file: " + MARKER, offset >= 0);
        fileText = fileText.substring(0, offset) + fileText.substring(offset + MARKER.length());

        myFile = parentDir == null ? createFile(myModule, fileName, fileText) : createFile(myModule, parentDir, fileName, fileText);

        /*PsiReference reference = myFile.findReferenceAt(offset - 1);
        if (reference instanceof PsiMultiReference) {
            PsiMultiReference multi = (PsiMultiReference) reference;
            PsiReference first = multi.getReferences()[0];
            return Pair.create(first.getElement(), offset);
        } else if (reference instanceof PsiReference) {
            //return Pair.create(reference.getElement(), offset + reference.getRangeInElement().getStartOffset());
            return Pair.create(reference.getElement(), offset);
        } */

        PsiElement element = myFile.findElementAt(offset - 1);
        assertNotNull("There is no PSI element at offset " + (offset - 1), element);
        return Pair.create(element, offset);
    }
}
