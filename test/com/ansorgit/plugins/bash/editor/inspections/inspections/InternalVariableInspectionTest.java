package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * @author jansorg
 */
public class InternalVariableInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("internalVariableInspection/ok", new InternalVariableInspection());
    }

    @Test
    public void testWriteAttempt() throws Exception {
        doTest("internalVariableInspection/writeAttempt", new InternalVariableInspection());
    }

    @Test
    public void testRedefinition() throws Exception {
        doTest("internalVariableInspection/redefinition", new InternalVariableInspection());
    }
}
