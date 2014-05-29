package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 19:47
 */
public class ReadOnlyVarInspectionTest extends AbstractInspectionTestCase {
    public ReadOnlyVarInspectionTest() {
        super(ReadonlyVariableInspection.class);
    }

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

}
