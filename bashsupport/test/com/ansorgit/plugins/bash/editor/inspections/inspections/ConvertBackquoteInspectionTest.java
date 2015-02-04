package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class ConvertBackquoteInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("convertBackquoteInspection/ok", ConvertBackquoteInspection.class, true);
    }

    public void testWarning() throws Exception {
        doTest("convertBackquoteInspection/warning", ConvertBackquoteInspection.class, true);
    }

    public void testNoOnTheFly() throws Exception {
        doTest("convertBackquoteInspection/notOnTheFly", ConvertBackquoteInspection.class, false);
    }
}