package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class SimpleArrayUseInspectionTest extends AbstractInspectionTestCase {
    public void testSimpleAccess() throws Exception {
        doTest("simpleArrayUseInspection/simpleAccess", new SimpleArrayUseInspection());
    }

    public void testSimpleAccessDeclared() throws Exception {
        doTest("simpleArrayUseInspection/simpleAccessDeclared", new SimpleArrayUseInspection());
    }

    public void testArrayAccess() throws Exception {
        doTest("simpleArrayUseInspection/arrayAccess", new SimpleArrayUseInspection());
    }

    public void testArrayAccessDeclared() throws Exception {
        doTest("simpleArrayUseInspection/arrayAccessDeclared", new SimpleArrayUseInspection());
    }

    public void testArrayStringUse() throws Exception {
        doTest("simpleArrayUseInspection/arrayStringUse", new SimpleArrayUseInspection());
    }
}
