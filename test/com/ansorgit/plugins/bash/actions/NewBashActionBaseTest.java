package com.ansorgit.plugins.bash.actions;

import org.junit.Assert;
import org.junit.Test;


public class NewBashActionBaseTest {
    @Test
    public void testComputeFilename() throws Exception {
        Assert.assertEquals("test.sh", NewBashFileAction.computeFilename("test"));
        Assert.assertEquals("test.bash", NewBashFileAction.computeFilename("test.bash"));
        Assert.assertEquals("test.bash.bash", NewBashFileAction.computeFilename("test.bash.bash"));
    }
}