package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Ignore;

public class WrapWordInStringInspectionTest extends AbstractInspectionTestCase {

    private WrapWordInStringInspection tool = new WrapWordInStringInspection();

    public void testOk() throws Exception {
        doTest("wrapWordStringInspection/ok", WrapWordInStringInspection.class, false);
    }

    @Ignore
    public void testWarning() throws Exception {
        //ignore for now
    //    doTest("wrapWordStringInspection/warning", tool);
    }
}