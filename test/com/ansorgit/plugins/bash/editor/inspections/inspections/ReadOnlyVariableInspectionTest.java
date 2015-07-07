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

}
