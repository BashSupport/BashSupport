package com.ansorgit.plugins.bash.util;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.SystemInfoRt;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class OSUtilTest {
    @Test
    public void testCygwinToNative() throws Exception {
        if (SystemInfo.isWindows) {
            Assert.assertEquals("c:\\cygwin\\bin\\bash.exe", OSUtil.bashCompatibleToNative("/cygdrive/c/cygwin/bin/bash.exe"));
        } else {
            Assert.assertEquals("c:/cygwin/bin/bash.exe", OSUtil.bashCompatibleToNative("/cygdrive/c/cygwin/bin/bash.exe"));
        }
    }

    @Test
    public void testFindBestExecutable() throws Exception {
        if (SystemInfoRt.isWindows) {
            String path = OSUtil.findBestExecutable("info");
            Assert.assertNotNull(path);
        }
    }

    @Test
    public void testFindBestExecutablePaths() throws Exception {
        if (SystemInfoRt.isWindows) {
            String path = OSUtil.findBestExecutable("info", Collections.singletonList("/usr/bin"));
            Assert.assertNotNull(path);

            path = OSUtil.findBestExecutable("info", Collections.singletonList("/path/which/is/not/here"));
            Assert.assertNull(path);
        }
    }
}