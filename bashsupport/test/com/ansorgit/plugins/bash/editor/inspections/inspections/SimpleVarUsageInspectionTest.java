package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 29.12.10
 * Time: 12:29
 */
public class SimpleVarUsageInspectionTest extends AbstractInspectionTestCase {
    public SimpleVarUsageInspectionTest() {
        super(SimpleVarUsageInspection.class);
    }

    public void testOk() throws Exception {
        doTest("simpleVarUsageInspection/ok", new SimpleVarUsageInspection());
    }

    public void testSimpleUse() throws Exception {
        doTest("simpleVarUsageInspection/simpleUse", new SimpleVarUsageInspection());
    }
}
