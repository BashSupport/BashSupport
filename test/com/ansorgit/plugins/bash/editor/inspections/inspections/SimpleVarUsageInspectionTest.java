package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.intellij.testFramework.LoggedErrorProcessor;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jansorg
 */
public class SimpleVarUsageInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("simpleVarUsageInspection/ok", new SimpleVarUsageInspection());
    }

    @Test
    public void testSimpleUse() throws Exception {
        doTest("simpleVarUsageInspection/simpleUse", new SimpleVarUsageInspection());
    }

    @Test
    @Ignore
    public void testIssue333Unquoted() throws Exception {
        final AtomicInteger errors = new AtomicInteger(0);

        LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor() {
            @Override
            public void processError(String message, Throwable t, String[] details, @NotNull Logger logger) {
                errors.incrementAndGet();
                throw new AssertionError(message);
            }
        });

        doTest("simpleVarUsageInspection/issue333Unquoted", new SimpleVarUsageInspection());

        LoggedErrorProcessor.restoreDefaultProcessor();

        Assert.assertEquals("No exceptions must occur during the var inspection", 0, errors.get());
    }

    @Test
    @Ignore
    public void testIssue333() throws Exception {
        final AtomicInteger errors = new AtomicInteger(0);

        LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor() {
            @Override
            public void processError(String message, Throwable t, String[] details, @NotNull Logger logger) {
                errors.incrementAndGet();
                throw new AssertionError(message);
            }
        });

        doTest("simpleVarUsageInspection/issue333", new SimpleVarUsageInspection());

        LoggedErrorProcessor.restoreDefaultProcessor();

        Assert.assertEquals("No exceptions must occur during the var inspection", 0, errors.get());
    }
}
