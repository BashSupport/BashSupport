/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashConfigurationType.java, Class: BashConfigurationType
 * Last modified: 2010-05-13
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * This code is based on the intellij-batch plugin.
 *
 * @author wibotwi, jansorg
 */
public class BashConfigurationType implements ConfigurationType {
    public String getDisplayName() {
        return "Bash";
    }

    public String getConfigurationTypeDescription() {
        return "Bash run configuration";
    }

    public Icon getIcon() {
        return BashIcons.BASH_FILE_ICON;
    }

    @NotNull
    public String getId() {
        return "BashConfigurationType";
    }

    public static BashConfigurationType getInstance() {
        ConfigurationType[] configurationTypes = Extensions.getExtensions(CONFIGURATION_TYPE_EP);

        for (ConfigurationType configurationType : configurationTypes) {
            if (configurationType instanceof BashConfigurationType) {
                return (BashConfigurationType) configurationType;
            }
        }

        throw new IllegalStateException("Invalid state in getInstance");
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new BashConfigurationFactory(this)};
    }

    private static class BashConfigurationFactory extends ConfigurationFactory {
        public BashConfigurationFactory(BashConfigurationType batchConfigurationType) {
            super(batchConfigurationType);
        }

        @Override
        public RunConfiguration createTemplateConfiguration(Project project) {
            BashRunConfiguration configuration = new BashRunConfiguration(new RunConfigurationModule(project), this, "");
            configuration.setInterpreterPath(new BashInterpreterDetection().findBestLocation());
            return configuration;
        }
    }
}
