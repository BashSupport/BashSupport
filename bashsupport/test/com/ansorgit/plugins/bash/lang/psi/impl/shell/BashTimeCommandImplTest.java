package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.intellij.psi.PsiElement;
import org.junit.Assert;

public class BashTimeCommandImplTest extends AbstractShellCommandTest {
    public void testTimeCommand() throws Exception {
        PsiElement command = configureCommand();

        //a bad implementation of the time command node made the psi element null
        Assert.assertNotNull(command);
    }
}