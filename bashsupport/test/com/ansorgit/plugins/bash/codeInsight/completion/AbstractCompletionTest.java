package com.ansorgit.plugins.bash.codeInsight.completion;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionTestCase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.TestDataFile;
import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: jansorg
 * Date: 09.02.11
 * Time: 21:11
 */
public abstract class AbstractCompletionTest extends CompletionTestCase {
    public static final String[] EMPTY = new String[0];

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/codeInsight/completion/" + getTestDir() + "/";
    }

    protected abstract String getTestDir();

    protected void configure() throws Exception {
        configure(1);
    }

    protected void configure(int invocationCount) throws Exception {
        configureByFileNoCompletion(getTestName(false) + ".bash");

        complete(invocationCount);
    }

    protected void configure(String... files) throws Exception {
        configure(1, files);
    }

    protected void configure(int invocationCount, String... files) throws Exception {
        configureByFileNoCompletion(getTestName(false) + ".bash");
        for (String file : files) {
            addFile(file);
        }

        complete(invocationCount);
    }

    protected PsiFile addFile(@TestDataFile @NonNls String filePath) throws Exception {
        final String fullPath = getTestDataPath() + filePath;
        final VirtualFile vFile = LocalFileSystem.getInstance().findFileByPath(fullPath.replace(File.separatorChar, '/'));
        assertNotNull("file " + filePath + " not found", vFile);

        String fileText = StringUtil.convertLineSeparators(VfsUtil.loadText(vFile));

        final String fileName = vFile.getName();

        return createFile(myModule, myFile.getVirtualFile().getParent(), fileName, fileText);
    }

    protected void checkItems(String... values) {
        if (myItems == null) {
            assertEquals(values.length, 0);
            return;
        }

        List<String> texts = Lists.transform(Lists.newArrayList(myItems), new Function<LookupElement, String>() {
            public String apply(LookupElement lookupElement) {
                return lookupElement.getLookupString();
            }
        });

        List<String> expected = Arrays.asList(values);

        assertEquals("Unexpected number of completions: " + texts, values.length, texts.size());

        ArrayList<String> remaining = Lists.newArrayList(expected);
        remaining.removeAll(texts);

        assertTrue("Not all completions were found, left over: " + remaining, texts.containsAll(expected) && expected.containsAll(texts));

        //assertEquals("Only the first index " + index + " matched of: " + Arrays.toString(myItems), values.length, index);
    }

    protected void complete(final int time) {
        //make sure with "false" that no auto-insertion of the completion is performed
        new CodeCompletionHandlerBase(CompletionType.BASIC, false, true).invokeCompletion(myProject, myEditor, myFile, time);

        LookupImpl lookup = (LookupImpl) LookupManager.getActiveLookup(myEditor);
        myItems = lookup == null ? null : lookup.getItems().toArray(LookupElement.EMPTY_ARRAY);
        myPrefix = lookup == null ? "" : lookup.getItems().get(0).getPrefixMatcher().getPrefix();
    }
}
