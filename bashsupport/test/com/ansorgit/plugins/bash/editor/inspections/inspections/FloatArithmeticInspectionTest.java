package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 16:50
 */
public class FloatArithmeticInspectionTest extends AbstractInspectionTestCase {
    public FloatArithmeticInspectionTest() {
        super(FloatArithmeticInspection.class);
    }

    public void testOk() throws Exception {
        doTest("floatArithmeticInspection/ok", new FloatArithmeticInspection());
    }

    public void testFloatArithmetic() throws Exception {
        doTest("floatArithmeticInspection/floatArithmetic", new FloatArithmeticInspection());
    }
}
