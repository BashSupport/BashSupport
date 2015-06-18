package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class DuplicateFunctionDefInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("duplicateFunctionDefInspection/ok", new DuplicateFunctionDefInspection());
    }

    @Test
    public void testDoubleDefinition() throws Exception {
        doTest("duplicateFunctionDefInspection/doubleDefinition", new DuplicateFunctionDefInspection());
    }

    @Test
    public void testTripleDefinition() throws Exception {
        doTest("duplicateFunctionDefInspection/tripleDefinition", new DuplicateFunctionDefInspection());
    }
}
