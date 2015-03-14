package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 16:50
 */
public class AddShebangInspectionTest extends AbstractInspectionTestCase {
    public AddShebangInspectionTest() {
        super(AddShebangInspection.class);
    }

    public void testOk() throws Exception {
        doTest("addShebangInspection/ok", new AddShebangInspection());
    }

    public void testEmptyFile() throws Exception {
        doTest("addShebangInspection/emptyFile", new AddShebangInspection());
    }

    public void testNonEmptyFile() throws Exception {
        doTest("addShebangInspection/nonEmptyFile", new AddShebangInspection());
    }
}
