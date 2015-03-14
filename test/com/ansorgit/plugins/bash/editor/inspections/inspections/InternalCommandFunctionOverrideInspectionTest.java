package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class InternalCommandFunctionOverrideInspectionTest extends AbstractInspectionTestCase {
    public void testOverride() throws Exception {
        doTest("internalCommandFunctionOverrideInspection/override", new InternalCommandFunctionOverrideInspection());
    }

    public void testNoOverride() throws Exception {
        doTest("internalCommandFunctionOverrideInspection/ok", new InternalCommandFunctionOverrideInspection());
    }
}
