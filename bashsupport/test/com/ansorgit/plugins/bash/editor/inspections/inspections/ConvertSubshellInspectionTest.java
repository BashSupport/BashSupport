package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class ConvertSubshellInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("convertSubshellInspection/ok", ConvertSubshellInspection.class, true);
    }

    public void testWarning() throws Exception {
        doTest("convertSubshellInspection/warning", ConvertSubshellInspection.class, true);
    }

    public void testNoOnTheFly() throws Exception {
        doTest("convertSubshellInspection/notOnTheFly", ConvertSubshellInspection.class, false);
    }
}