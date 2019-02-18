package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author jansorg
 */
public class MissingIncludeFileInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() {
        doTest("missingIncludeFileInspection/ok", new MissingIncludeFileInspection());
    }

    @Test
    public void testHomeDir() throws Exception {
        String homePath = System.getenv("HOME");
        if (homePath.isEmpty()) {
            //unable to run test properly
            return;
        }

        Path file = Paths.get(homePath, "bashsupport.bash");
        try {
            Files.createFile(file);
            doTest("missingIncludeFileInspection/homeDir", new MissingIncludeFileInspection());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    public void testHomeDirNoLocalValidation() {
        String homePath = System.getenv("HOME");
        if (homePath.isEmpty()) {
            //unable to run test properly
            return;
        }

        BashProjectSettings settings = BashProjectSettings.storedSettings(getProject());
        boolean old = settings.isValidateWithCurrentEnv();
        try {
            settings.setValidateWithCurrentEnv(false);
            doTest("missingIncludeFileInspection/homeDirNoLocalValidation", new MissingIncludeFileInspection());
        } finally {
            settings.setValidateWithCurrentEnv(old);
        }
    }

    @Test
    public void testMissingFile() {
        doTest("missingIncludeFileInspection/missingFile", new MissingIncludeFileInspection());
    }

    @Test
    public void testIncludeDirectory() {
        doTest("missingIncludeFileInspection/includeDirectory", new MissingIncludeFileInspection());
    }

    @Test
    public void testInvalidIncludePath() {
        doTest("missingIncludeFileInspection/invalidIncludePath", new MissingIncludeFileInspection());
    }
}
