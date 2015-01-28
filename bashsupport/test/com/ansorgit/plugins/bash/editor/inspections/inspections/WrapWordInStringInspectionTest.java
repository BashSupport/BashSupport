package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class WrapWordInStringInspectionTest extends AbstractInspectionTestCase {

    private WrapWordInStringInspection tool;

    public void testOk() throws Exception {
        tool = new WrapWordInStringInspection();
        doTest("wrapWordStringInspection/ok", tool);
    }

    public void testWarning() throws Exception {
        doTest("wrapWordStringInspection/ok", tool);
    }
}