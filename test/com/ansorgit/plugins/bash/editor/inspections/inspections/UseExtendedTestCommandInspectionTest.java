package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class UseExtendedTestCommandInspectionTest extends AbstractInspectionTestCase {

    @Test
    public void testOk() {
        doTest("useExtendedTestCommand/ok", UseExtendedTestCommandInspection.class, true);
    }

    @Test
    public void testWarning() {
        doTest("useExtendedTestCommand/warning", UseExtendedTestCommandInspection.class, true);
    }

    @Test
    public void testNoOnTheFly() {
        doTest("useExtendedTestCommand/notOnTheFly", UseExtendedTestCommandInspection.class, false);
    }

}