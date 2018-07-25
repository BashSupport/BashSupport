package com.ansorgit.plugins.bash.runner;

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
        GeneralCommandLine cmd = new GeneralCommandLine();
        cmd.setExePath(runConfig.getInterpreterPath());
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
