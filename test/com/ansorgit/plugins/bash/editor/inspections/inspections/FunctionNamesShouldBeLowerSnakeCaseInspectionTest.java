package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class FunctionNamesShouldBeLowerSnakeCaseInspectionTest extends AbstractInspectionTestCase {

    public void testOverride() {
        doTest("functionNamesShouldBeLowerSnakeCaseInspection/invalid", new FunctionNamesShouldBeLowerSnakeCaseInspection());
    }

    public void testNoOverride() {
        doTest("functionNamesShouldBeLowerSnakeCaseInspection/ok", new FunctionNamesShouldBeLowerSnakeCaseInspection());
    }
}
