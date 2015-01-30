package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class DuplicateFunctionDefInspectionTest extends AbstractInspectionTestCase {
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
