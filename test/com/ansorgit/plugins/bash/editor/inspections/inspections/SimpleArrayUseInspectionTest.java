package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class SimpleArrayUseInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testSimpleAccess() throws Exception {
        doTest("simpleArrayUseInspection/simpleAccess", new SimpleArrayUseInspection());
    }

    @Test
    public void testSimpleAccessDeclared() throws Exception {
        doTest("simpleArrayUseInspection/simpleAccessDeclared", new SimpleArrayUseInspection());
    }

    @Test
    public void testArrayAccess() throws Exception {
        doTest("simpleArrayUseInspection/arrayAccess", new SimpleArrayUseInspection());
    }

    @Test
    public void testArrayAccessDeclared() throws Exception {
        doTest("simpleArrayUseInspection/arrayAccessDeclared", new SimpleArrayUseInspection());
    }

    @Test
    public void testArrayStringUse() throws Exception {
        doTest("simpleArrayUseInspection/arrayStringUse", new SimpleArrayUseInspection());
    }
}
