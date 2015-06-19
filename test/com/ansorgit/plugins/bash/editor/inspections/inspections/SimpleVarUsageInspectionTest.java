package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * User: jansorg
 * Date: 29.12.10
 * Time: 12:29
 */
public class SimpleVarUsageInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("simpleVarUsageInspection/ok", new SimpleVarUsageInspection());
    }

    @Test
    public void testSimpleUse() throws Exception {
        doTest("simpleVarUsageInspection/simpleUse", new SimpleVarUsageInspection());
    }
}
