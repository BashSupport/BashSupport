package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 16:50
 */
public class DuplicateFunctionDefInspectionTest extends AbstractInspectionTestCase {
    public DuplicateFunctionDefInspectionTest() {
        super(DuplicateFunctionDefInspection.class);
    }

    public void testOk() throws Exception {
        doTest("duplicateFunctionDefInspection/ok", new DuplicateFunctionDefInspection());
    }

    public void testDoubleDefinition() throws Exception {
        doTest("duplicateFunctionDefInspection/doubleDefinition", new DuplicateFunctionDefInspection());
    }

    public void testTripleDefinition() throws Exception {
        doTest("duplicateFunctionDefInspection/tripleDefinition", new DuplicateFunctionDefInspection());
    }
}
