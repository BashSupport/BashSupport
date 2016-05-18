package com.ansorgit.plugins.bash.lang.util;

import org.junit.Assert;
import org.junit.Test;

public class HeredocSharedImplTest {
    @Test
    public void testStartOffset() throws Exception {
        Assert.assertEquals(0, HeredocSharedImpl.startMarkerTextOffset("$", false));
        Assert.assertEquals(0, HeredocSharedImpl.startMarkerTextOffset("$ABC", false));

        Assert.assertEquals(1, HeredocSharedImpl.startMarkerTextOffset("\"ABC\"", false));
        Assert.assertEquals(2, HeredocSharedImpl.startMarkerTextOffset("$\"ABC\"", false));
    }

    @Test
    public void testStartOffsetTabs() throws Exception {
        Assert.assertEquals(0, HeredocSharedImpl.startMarkerTextOffset("$", true));
        Assert.assertEquals(2, HeredocSharedImpl.startMarkerTextOffset("\t\t$", true));
        Assert.assertEquals(3, HeredocSharedImpl.startMarkerTextOffset("\t\t\t$ABC", true));

        Assert.assertEquals(0, HeredocSharedImpl.startMarkerTextOffset("\t\"ABC\"", false));
        Assert.assertEquals(0, HeredocSharedImpl.startMarkerTextOffset("\t$\"ABC\"", false));
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

    @Test
    public void testIssue331() throws Exception {
        Assert.assertEquals(0, HeredocSharedImpl.startMarkerTextOffset("\t\t", false));

        Assert.assertEquals(1, HeredocSharedImpl.startMarkerTextOffset("\t\t", true));
    }
}