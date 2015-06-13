package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Ignore;
import org.junit.Test;

public class WrapWordInStringInspectionTest extends AbstractInspectionTestCase {

    private WrapWordInStringInspection tool = new WrapWordInStringInspection();

    @Test
    public void testOk() throws Exception {
        doTest("wrapWordStringInspection/ok", WrapWordInStringInspection.class, false);
    }

    @Ignore
    @Test
    public void testWarning() throws Exception {
        //ignore for now
    //    doTest("wrapWordStringInspection/warning", tool);
    }
}