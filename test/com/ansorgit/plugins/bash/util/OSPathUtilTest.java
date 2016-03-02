package com.ansorgit.plugins.bash.util;

import junit.framework.Assert;
import org.junit.Test;

public class OSPathUtilTest {

    @Test
    public void testCygwinToNative() throws Exception {
        Assert.assertEquals("c:\\cygwin\\bin\\bash.exe", OSPathUtil.bashCompatibleToNative("/cygdrive/c/cygwin/bin/bash.exe"));
    }
}