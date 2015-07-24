package com.ansorgit.plugins.bash.lang.util;

import org.junit.Assert;
import org.junit.Test;

public class HeredocSharedImplTest {

    public void testCleanMarker() throws Exception {

    }

    @Test
    public void testStartOffset() throws Exception {
        Assert.assertEquals(0, HeredocSharedImpl.startMarkerTextOffset("$"));
        Assert.assertEquals(0, HeredocSharedImpl.startMarkerTextOffset("$ABC"));

        Assert.assertEquals(1, HeredocSharedImpl.startMarkerTextOffset("\"ABC\""));
        Assert.assertEquals(2, HeredocSharedImpl.startMarkerTextOffset("$\"ABC\""));
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