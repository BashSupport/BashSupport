package com.ansorgit.plugins.bash.lang.lexer;

import org.junit.Assert;
import org.junit.Test;

/**
 */
public class HeredocMarkerInfoTest {
    @Test
    public void testIsEmpty() throws Exception {
        HeredocLexingState state = new HeredocLexingState();
        Assert.assertTrue(state.isEmpty());

        state.pushMarker(0, "NAME", false);
        Assert.assertFalse(state.isEmpty());
        state.popMarker("NAME");

        Assert.assertTrue(state.isEmpty());
    }

    @Test
    public void testTabs() throws Exception {
        HeredocLexingState state = new HeredocLexingState();

        state.pushMarker(0, "NAME", false);
        Assert.assertFalse(state.isIgnoringTabs());
        state.popMarker("NAME");

        state.pushMarker(0, "NAME", true);
        Assert.assertTrue(state.isIgnoringTabs());
        state.popMarker("NAME");

        Assert.assertTrue(state.isEmpty());
    }

    @Test
    public void testMarkerMatching() throws Exception {
        HeredocLexingState state = new HeredocLexingState();

        state.pushMarker(0, "NAME", false);
        Assert.assertTrue(state.isNextMarker("NAME"));
        Assert.assertFalse(state.isNextMarker("\tNAME"));
    }

    @Test
    public void testMarkerMatchingIngoredTabs() throws Exception {
        HeredocLexingState state = new HeredocLexingState();

        state.pushMarker(0, "NAME", true);
        Assert.assertTrue(state.isNextMarker("NAME"));
        Assert.assertTrue(state.isNextMarker("\tNAME"));
    }
}