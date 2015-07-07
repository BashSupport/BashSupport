package com.ansorgit.plugins.bash.lang.psi.fileInclude;

import org.junit.Test;

public class FunctionResolveFileIncludeTestCase extends AbstractFileIncludeTest {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "functionResolve/";
    }

    @Test
    public void testSimpleResolve1() throws Exception {
        checkWithIncludeFile("includedFile.bash", true);
    }

    @Test
    public void testSimpleResolve2() throws Exception {
        checkWithIncludeFile("includedFile.bash", true);
    }
}
