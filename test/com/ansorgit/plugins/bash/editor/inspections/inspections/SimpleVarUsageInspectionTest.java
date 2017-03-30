/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.settings.BashProjectSettings;
 import com.google.common.collect.Lists;
import com.intellij.testFramework.LoggedErrorProcessor;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
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

    /**
     * Checks whether the experimental settings has any effect
     *
     * @throws Exception
     */
    @Test
    @Ignore("unknown how this should throw exceptions")
    public void _testIssue333BrokenWithSettings() throws Exception {
        BashProjectSettings.storedSettings(getProject()).setEvalEscapesEnabled(true);

        try {
            final AtomicInteger errors = new AtomicInteger(0);
            final List<String> errorMessages = Lists.newArrayList();

            LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor() {
                @Override
                public void processError(String message, Throwable t, String[] details, @NotNull Logger logger) {
                    errors.incrementAndGet();
                    errorMessages.add(message);
                    throw new AssertionError(message);
                }
            });

            doTest("simpleVarUsageInspection/issue333", new SimpleVarUsageInspection());

            LoggedErrorProcessor.restoreDefaultProcessor();

            Assert.assertEquals("No exceptions must occur during the var inspection: " + errorMessages, 4, errors.get());
        } finally {
            BashProjectSettings.storedSettings(getProject()).setEvalEscapesEnabled(false);
        }
    }
}
