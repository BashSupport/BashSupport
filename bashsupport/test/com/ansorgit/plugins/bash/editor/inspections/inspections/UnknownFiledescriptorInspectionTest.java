package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class UnknownFiledescriptorInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("unknownFileDescriptorInspection/ok", UnknownFiledescriptorInspection.class, false);
    }

    public void testWarning() throws Exception {
        doTest("unknownFileDescriptorInspection/warning", UnknownFiledescriptorInspection.class, false);
    }
}