package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * @author jansorg
 */
public class ReadOnlyVariableInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("readOnlyVarInspection/ok", new ReadonlyVariableInspection());
    }

    @Test
    public void testSimpleVar() throws Exception {
        doTest("readOnlyVarInspection/simpleVar", new ReadonlyVariableInspection());
    }

    @Test
    public void testTypesetVar() throws Exception {
        doTest("readOnlyVarInspection/typesetVar", new ReadonlyVariableInspection());
    }

    @Test
    public void testDeclaredVar() throws Exception {
        doTest("readOnlyVarInspection/declaredVar", new ReadonlyVariableInspection());
    }

    @Test
    public void testIssue229() throws Exception {
        doTest("readOnlyVarInspection/issue229", new ReadonlyVariableInspection());
    }

    @Test
    public void testIssue263() throws Exception {
        doTest("readOnlyVarInspection/issue263", new ReadonlyVariableInspection());
    }

    @Test
    public void testIssue263_included() throws Exception {
        doTest("readOnlyVarInspection/issue263_included", new ReadonlyVariableInspection());
    }

    @Test
    public void testIssue263_eval() throws Exception {
        doTest("readOnlyVarInspection/issue263_eval", new ReadonlyVariableInspection());
    }

    @Test
    public void testIssue298() throws Exception {
        doTest("readOnlyVarInspection/issue298", new ReadonlyVariableInspection());
    }

    @Test
    public void testIssue586() throws Exception {
        doTest("readOnlyVarInspection/issue586", new ReadonlyVariableInspection());
    }
}
