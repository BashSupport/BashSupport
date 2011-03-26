package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 19:47
 */
public class ArrayUseOfSimpleVarInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testSimpleAccess() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/simpleVar", new ArrayUseOfSimpleVarInspection());
    }

    @Test
    public void testSimpleAccessDeclared() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/simpleVarDeclared", new ArrayUseOfSimpleVarInspection());
    }

    @Test
    public void testArrayAccess() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/arrayVar", new ArrayUseOfSimpleVarInspection());
    }

    @Test
    public void testArrayAccessDeclared() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/arrayVarDeclared", new ArrayUseOfSimpleVarInspection());
    }
}
