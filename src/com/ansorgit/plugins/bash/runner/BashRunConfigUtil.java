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

import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.util.ProgramParametersUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author jansorg
 */
public final class BashRunConfigUtil {
    private BashRunConfigUtil() {
    }

    @NotNull
    public static GeneralCommandLine createCommandLine(String workingDir, BashRunConfiguration runConfig) {
        String interpreterPath;
        if (runConfig.isUseProjectInterpreter()) {
            interpreterPath = BashProjectSettings.storedSettings(runConfig.getProject()).getProjectInterpreter();
        } else {
            interpreterPath = runConfig.getInterpreterPath();
        }

        GeneralCommandLine cmd = new GeneralCommandLine();
        cmd.setExePath(interpreterPath);
        cmd.getParametersList().addParametersString(runConfig.getInterpreterOptions());

        cmd.addParameter(runConfig.getScriptName());
        cmd.getParametersList().addParametersString(runConfig.getProgramParameters());

        cmd.withWorkDirectory(workingDir);
        cmd.withParentEnvironmentType(runConfig.isPassParentEnvs() ? GeneralCommandLine.ParentEnvironmentType.CONSOLE : GeneralCommandLine.ParentEnvironmentType.NONE);
        cmd.withEnvironment(runConfig.getEnvs());
        return cmd;
    }

    public static String findWorkingDir(BashRunConfiguration runConfig) {
        return ProgramParametersUtil.getWorkingDir(runConfig, runConfig.getProject(), runConfig.getConfigurationModule().getModule());
    }
}
