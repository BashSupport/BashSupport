package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class ConvertSubshellInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("convertSubshellInspection/ok", ConvertSubshellInspection.class, true);
    }

    @Test
    public void testWarning() throws Exception {
        doTest("convertSubshellInspection/warning", ConvertSubshellInspection.class, true);
    }

    @Test
    public void testNoOnTheFly() throws Exception {
        doTest("convertSubshellInspection/notOnTheFly", ConvertSubshellInspection.class, false);
    }
}