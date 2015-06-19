package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 19:47
 */
public class ReadOnlyVariableInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("readOnlyVarInspection/ok", new ReadonlyVariableInspection());
    }

    public void testSimpleVar() throws Exception {
        doTest("readOnlyVarInspection/simpleVar", new ReadonlyVariableInspection());
    }

    public void testTypesetVar() throws Exception {
        doTest("readOnlyVarInspection/typesetVar", new ReadonlyVariableInspection());
    }

    public void testDeclaredVar() throws Exception {
        doTest("readOnlyVarInspection/declaredVar", new ReadonlyVariableInspection());
    }

    public void testIssue229() throws Exception {
        doTest("readOnlyVarInspection/issue229", new ReadonlyVariableInspection());
    }

}
