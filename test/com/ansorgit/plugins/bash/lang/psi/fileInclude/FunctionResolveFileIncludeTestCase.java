package com.ansorgit.plugins.bash.lang.psi.fileInclude;

public class FunctionResolveFileIncludeTestCase extends AbstractFileIncludeTest {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "functionResolve/";
    }

    public void testSimpleResolve1() throws Exception {
        checkWithIncludeFile("includedFile.bash", true);
    }

    public void testSimpleResolve2() throws Exception {
        checkWithIncludeFile("includedFile.bash", true);
    }
}
