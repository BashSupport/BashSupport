package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 26.03.11
 * Time: 23:05
 */
public class InternalCommandFunctionOverrideInspectionTest extends AbstractInspectionTestCase {
    public InternalCommandFunctionOverrideInspectionTest() {
        super(InternalCommandFunctionOverrideInspection.class);
    }

    public void testOverride() throws Exception {
        doTest("internalCommandFunctionOverrideInspection/override", new InternalCommandFunctionOverrideInspection());
    }

    public void testNoOverride() throws Exception {
        doTest("internalCommandFunctionOverrideInspection/ok", new InternalCommandFunctionOverrideInspection());
    }
}
