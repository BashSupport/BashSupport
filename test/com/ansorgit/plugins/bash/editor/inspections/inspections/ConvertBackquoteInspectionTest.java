package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class ConvertBackquoteInspectionTest extends AbstractInspectionTestCase {

    @Test
    public void testOk() throws Exception {
        doTest("convertBackquoteInspection/ok", ConvertBackquoteInspection.class, true);
    }

    @Test
    public void testWarning() throws Exception {
        doTest("convertBackquoteInspection/warning", ConvertBackquoteInspection.class, true);
    }

    @Test
    public void testNoOnTheFly() throws Exception {
        doTest("convertBackquoteInspection/notOnTheFly", ConvertBackquoteInspection.class, false);
    }
}