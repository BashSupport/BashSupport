package com.ansorgit.plugins.bash.actions;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.junit.Assert;
import org.junit.Test;

public class BashTemplatesFactoryTest extends BashCodeInsightFixtureTestCase {
    @Test
    public void testSimpleCreate() throws Exception {
        PsiFile newScript = createNewBashFile(findSrcDir(), "new_file.bash", BashTemplatesFactory.DEFAULT_TEMPLATE_FILENAME);
        Assert.assertNotNull(newScript);
        Assert.assertEquals("new_file.bash", newScript.getName());
        Assert.assertEquals("#!/usr/bin/env bash", newScript.getText());
    }

    @Test
    public void testEmptyExtension() throws Exception {
        PsiFile newScript = createNewBashFile(findSrcDir(), "new_file", BashTemplatesFactory.DEFAULT_TEMPLATE_FILENAME);
        Assert.assertNotNull(newScript);
        Assert.assertEquals("new_file", newScript.getName());
        Assert.assertEquals("#!/usr/bin/env bash", newScript.getText());
    }

    @Test
    public void testInvalidTemplate() throws Exception {
        try {
            createNewBashFile(findSrcDir(), "new_file", "not-existing-template");
            Assert.fail("Template must not exist");
        } catch (Throwable e) {
            Assert.assertTrue(e.getMessage().contains("Template not found: not-existing-template"));
        }
    }

    @Test
    public void testTemplateDescriptor() throws Exception {


    }

    private PsiDirectory findSrcDir() {
        return myFixture.configureByText(FileTypes.PLAIN_TEXT, "dummy content").getContainingDirectory();
    }

    private static PsiFile createNewBashFile(final PsiDirectory srcDir, final String fileName, final String defaultTemplateFilename) {
        return ApplicationManager.getApplication().runWriteAction(new Computable<PsiFile>() {
            @Override
            public PsiFile compute() {
                return BashTemplatesFactory.createFromTemplate(srcDir, fileName, defaultTemplateFilename);
            }
        });
    }
}