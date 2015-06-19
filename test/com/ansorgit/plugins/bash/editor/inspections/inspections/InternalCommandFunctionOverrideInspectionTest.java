package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class InternalCommandFunctionOverrideInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOverride() throws Exception {
        doTest("internalCommandFunctionOverrideInspection/override", new InternalCommandFunctionOverrideInspection());
    }

    @Test
    public void testNoOverride() throws Exception {
        doTest("internalCommandFunctionOverrideInspection/ok", new InternalCommandFunctionOverrideInspection());
    }
}
