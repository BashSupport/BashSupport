package com.ansorgit.plugins.bash.lang.util;

import org.junit.Assert;
import org.junit.Test;

public class HeredocSharedImplTest {

    public void testCleanMarker() throws Exception {

    }

    @Test
    public void testWrapMarker() throws Exception {
        Assert.assertEquals("EOF_NEW", HeredocSharedImpl.wrapMarker("EOF_NEW", "EOF"));
        Assert.assertEquals("\"EOF_NEW\"", HeredocSharedImpl.wrapMarker("EOF_NEW", "\"EOF\""));
        Assert.assertEquals("\'EOF_NEW\'", HeredocSharedImpl.wrapMarker("EOF_NEW", "\'EOF\'"));
        Assert.assertEquals("\\EOF_NEW", HeredocSharedImpl.wrapMarker("EOF_NEW", "\\EOF"));

        Assert.assertEquals("$\"EOF_NEW\"", HeredocSharedImpl.wrapMarker("EOF_NEW", "$\"EOF\""));
        Assert.assertEquals("$\'EOF_NEW\'", HeredocSharedImpl.wrapMarker("EOF_NEW", "$\'EOF\'"));
    }
}