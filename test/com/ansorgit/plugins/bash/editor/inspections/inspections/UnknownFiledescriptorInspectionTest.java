package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class UnknownFiledescriptorInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("unknownFileDescriptorInspection/ok", UnknownFiledescriptorInspection.class, false);
    }

    @Test
    public void testWarning() throws Exception {
        doTest("unknownFileDescriptorInspection/warning", UnknownFiledescriptorInspection.class, false);
    }
}