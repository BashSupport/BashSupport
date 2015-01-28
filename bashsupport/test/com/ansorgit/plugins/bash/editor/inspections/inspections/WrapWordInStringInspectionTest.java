package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class WrapWordInStringInspectionTest extends AbstractInspectionTestCase {

    private WrapWordInStringInspection tool = new WrapWordInStringInspection();

    public void testOk() throws Exception {
        doTest("wrapWordStringInspection/ok", WrapWordInStringInspection.class);
    }

    public void testWarning() throws Exception {
        doTest("wrapWordStringInspection/warning", tool);
    }
}