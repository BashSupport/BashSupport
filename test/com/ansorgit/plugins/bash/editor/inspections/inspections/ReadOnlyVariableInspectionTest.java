package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 19:47
 */
public class ReadOnlyVariableInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("readOnlyVarInspection/ok", new ReadOnlyVariableInspection());
    }

    @Test
    public void testSimpleVar() throws Exception {
        doTest("readOnlyVarInspection/simpleVar", new ReadOnlyVariableInspection());
    }

    @Test
    public void testTypesetVar() throws Exception {
        doTest("readOnlyVarInspection/typesetVar", new ReadOnlyVariableInspection());
    }

    @Test
    public void testDeclaredVar() throws Exception {
        doTest("readOnlyVarInspection/declaredVar", new ReadOnlyVariableInspection());
    }

    @Test
    public void testIssue229() throws Exception {
        doTest("readOnlyVarInspection/issue229", new ReadOnlyVariableInspection());
    }

    @Test
    public void testIssue263() throws Exception {
        doTest("readOnlyVarInspection/issue263", new ReadOnlyVariableInspection());
    }

}
