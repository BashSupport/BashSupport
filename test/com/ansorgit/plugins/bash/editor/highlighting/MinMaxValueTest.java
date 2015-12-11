package com.ansorgit.plugins.bash.editor.highlighting;

import org.junit.Assert;
import org.junit.Test;

public class MinMaxValueTest {
    @Test
    public void testValues() throws Exception {
        MinMaxValue value = new MinMaxValue();
        value.add(1);
        value.add(1);
        value.add(10);
        value.add(3);
        value.add(0);

        Assert.assertEquals(0, value.min());
        Assert.assertEquals(10, value.max());
        Assert.assertEquals(3.0, value.average(), 0.01);
    }
}