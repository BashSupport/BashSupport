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

import com.ansorgit.plugins.bash.util.BashIcons;
import com.ansorgit.plugins.bash.util.BashInterpreterDetection;
import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.RunManagerEx;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.PossiblyDumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * Bash run configuration type.
 *
 * @author jansorg
 */
public class BashConfigurationType extends ConfigurationTypeBase implements PossiblyDumbAware {
    public BashConfigurationType() {
        super("BashConfigurationType", "Bash", "Bash run configuration", BashIcons.BASH_FILE_ICON);

        addFactory(new BashConfigurationFactory(this));
    }

    @NotNull
    public static BashConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(BashConfigurationType.class);
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }

    public static class BashConfigurationFactory extends ConfigurationFactoryEx {
        BashConfigurationFactory(BashConfigurationType configurationType) {
            super(configurationType);
        }

        @Override
        public void onNewConfigurationCreated(@NotNull RunConfiguration configuration) {
            //the last param has to be false because we do not want a fallback to the template (we're creating it right now) (avoiding a SOE)
            RunManagerEx.getInstanceEx(configuration.getProject()).setBeforeRunTasks(configuration, Collections.<BeforeRunTask>emptyList(), false);
        }

        @NotNull
        @Override
        public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
            return new BashRunConfiguration("", new RunConfigurationModule(project), this);
        }
    }
}
