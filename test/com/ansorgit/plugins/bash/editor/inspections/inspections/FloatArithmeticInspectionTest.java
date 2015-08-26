package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 16:50
 */
public class FloatArithmeticInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("floatArithmeticInspection/ok", new FloatArithmeticInspection());
    }

    @Test
    public void testFloatArithmetic() throws Exception {
        doTest("floatArithmeticInspection/floatArithmetic", new FloatArithmeticInspection());
    }

    @Test
    public void testIssue265() throws Exception {
        doTest("floatArithmeticInspection/issue265", new FloatArithmeticInspection());
    }
}
