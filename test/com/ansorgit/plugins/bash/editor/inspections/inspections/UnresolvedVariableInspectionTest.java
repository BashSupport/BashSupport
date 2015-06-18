package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class UnresolvedVariableInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("unresolvedVariableInspection/ok", UnresolvedVariableInspection.class, false);
    }

    @Test
    public void testWarning() throws Exception {
        doTest("unresolvedVariableInspection/warning", UnresolvedVariableInspection.class, false);
    }
}