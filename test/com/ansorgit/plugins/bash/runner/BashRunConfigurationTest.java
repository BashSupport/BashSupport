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

package com.ansorgit.plugins.bash.runner;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.execution.configurations.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * @author jansorg
 */
public class BashRunConfigurationTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testBuildOption() throws Exception {
        //dummy setup
        BashRunConfiguration config = new BashRunConfiguration("Bash", new RunConfigurationModule(getProject()), new ConfigurationFactory(BashConfigurationType.getInstance()) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new UnknownRunConfiguration(this, project);
            }
        });

        Assert.assertFalse("The make step must not be enabled by default", config.isCompileBeforeLaunchAddedByDefault());
    }

    @Test
    public void testInvalidInterpreterPath() throws Exception {
        //dummy setup
        BashRunConfiguration config = new BashRunConfiguration("Bash", new RunConfigurationModule(getProject()), BashConfigurationType.getInstance().getConfigurationFactories()[0]);
        config.setUseProjectInterpreter(false);
        config.setInterpreterPath("\"C:\\Program Files\\Git\\bin\\sh.exe\" -login -i");

        // must not throw an error about the invalid path
        try {
            config.checkConfiguration();
            Assert.fail("expected warning about invalid interpreter path");
        } catch (RuntimeConfigurationWarning e) {
            //expectd
        }
    }

    @Test
    public void testInvalidProjectInterpreterPath() throws Exception {
        //dummy setup
        BashRunConfiguration config = new BashRunConfiguration("Bash", new RunConfigurationModule(getProject()), BashConfigurationType.getInstance().getConfigurationFactories()[0]);
        config.setUseProjectInterpreter(true);

        BashProjectSettings.storedSettings(getProject()).setProjectInterpreter("invalid path");

        // must not throw an error about the invalid path
        try {
            config.checkConfiguration();
            Assert.fail("expected warning about invalid project interpreter path");
        } catch (RuntimeConfigurationWarning e) {
            //expectd
        }
    }

    @Test
    public void testNoProjectInterpreter() throws Exception {
        //dummy setup
        BashRunConfiguration config = new BashRunConfiguration("Bash", new RunConfigurationModule(getProject()), BashConfigurationType.getInstance().getConfigurationFactories()[0]);
        config.setUseProjectInterpreter(true);

        BashProjectSettings.storedSettings(getProject()).setProjectInterpreter("");

        // must not throw an error about the invalid path
        try {
            config.checkConfiguration();
            Assert.fail("expected warning about missing project interpreter path");
        } catch (RuntimeConfigurationError e) {
            //expectd
        }
    }

    @Test
    public void testSuggestedName() throws Exception {
        BashRunConfiguration config = new BashRunConfiguration("Bash", new RunConfigurationModule(getProject()), BashConfigurationType.getInstance().getConfigurationFactories()[0]);
        Assert.assertNull(config.suggestedName());

        config.setScriptName("");
        Assert.assertNull(config.suggestedName());

        config.setScriptName("test.bash");
        Assert.assertEquals("test", config.suggestedName());

        config.setScriptName(Paths.get("parent", "test.bash").toString());
        Assert.assertEquals("test", config.suggestedName());
    }
}