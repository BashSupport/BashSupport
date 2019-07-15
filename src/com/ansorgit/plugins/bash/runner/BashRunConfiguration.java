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

import com.ansorgit.plugins.bash.runner.terminal.BashTerminalRunConfigurationService;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configuration.AbstractRunConfiguration;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.ProgramParametersUtil;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * This code is based on the intellij-batch plugin.
 *
 * @author wibotwi, jansorg
 */
public class BashRunConfiguration extends AbstractRunConfiguration implements BashRunConfigurationParams, RunConfigurationWithSuppressedDefaultDebugAction {
    private String interpreterOptions = "";
    private String workingDirectory = "";
    private String interpreterPath = "";
    private boolean useProjectInterpreter = false;
    private String scriptName;
    private String programsParameters;

    BashRunConfiguration(String name, RunConfigurationModule module, ConfigurationFactory configurationFactory) {
        super(name, module, configurationFactory);
    }

    @Override
    public Collection<Module> getValidModules() {
        return getAllModules();
    }

    //@Override //removed in 183.x
    public boolean isCompileBeforeLaunchAddedByDefault() {
        return false;
    }

    @Override
    public boolean excludeCompileBeforeLaunchOption() {
        return false;
    }

    @NotNull
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new BashRunConfigurationEditor(getConfigurationModule().getModule());
    }

    @Nullable
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        BashTerminalRunConfigurationService service = ServiceManager.getService(BashTerminalRunConfigurationService.class);
        if (service != null && BashProjectSettings.storedSettings(getProject()).isUseTerminalPlugin()) {
            // the plugin is enabled, the experimental feature is enabled, too
            return service.getState(this, executor, env);
        }

        BashCommandLineState state = new BashCommandLineState(this, env);
        state.getConsoleBuilder().addFilter(new BashLineErrorFilter(getProject()));
        return state;
    }

    public String getInterpreterPath() {
        return interpreterPath;
    }

    public void setInterpreterPath(String path) {
        this.interpreterPath = path;
    }

    @Override
    public boolean isUseProjectInterpreter() {
        return useProjectInterpreter;
    }

    @Override
    public void setUseProjectInterpreter(boolean useProjectInterpreter) {
        this.useProjectInterpreter = useProjectInterpreter;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        Project project = getProject();

        Module module = getConfigurationModule().getModule();
        if (module != null) {
            //a missing module will cause a NPE in the check method
            ProgramParametersUtil.checkWorkingDirectoryExist(this, project, module);
        }

        if (useProjectInterpreter) {
            BashProjectSettings settings = BashProjectSettings.storedSettings(project);
            String interpreter = settings.getProjectInterpreter();
            if (interpreter.isEmpty()) {
                throw new RuntimeConfigurationError("No project interpreter configured");
            }

            Path path;
            try {
                path = Paths.get(interpreter);
            } catch (InvalidPathException e) {
                path = null;
            }
            if (path == null || !Files.isRegularFile(path) || !Files.isReadable(path)) {
                throw new RuntimeConfigurationWarning("Project interpreter path is invalid or not readable.");
            }
        } else {
            if (StringUtil.isEmptyOrSpaces(interpreterPath)) {
                throw new RuntimeConfigurationException("No interpreter path given.");
            }

            Path interpreterFile;
            try {
                interpreterFile = Paths.get(interpreterPath);
            } catch (InvalidPathException e) {
                interpreterFile = null;
                // don't warn on interpreter paths we can't handle, e.g.
                //      "C:\Program Files\Git\bin\sh.exe" -login -i
            }
            if (interpreterFile == null || !Files.isRegularFile(interpreterFile) || !Files.isReadable(interpreterFile)) {
                throw new RuntimeConfigurationWarning("Interpreter path is invalid or not readable.");
            }
        }

        if (StringUtil.isEmptyOrSpaces(scriptName)) {
            throw new RuntimeConfigurationError("Script name not given.");
        }
    }

    @Override
    public String suggestedName() {
        if (scriptName == null || scriptName.isEmpty()) {
            return null;
        }

        try {
            Path fileName = (Paths.get(scriptName)).getFileName();
            if (fileName == null) {
                return null;
            }

            String name = fileName.toString();

            int ind = name.lastIndexOf('.');
            if (ind != -1) {
                return name.substring(0, ind);
            }
            return name;
        } catch (InvalidPathException e) {
            return null;
        }
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);

        DefaultJDOMExternalizer.readExternal(this, element);
        readModule(element);
        EnvironmentVariablesComponent.readExternal(element, getEnvs());

        // common config
        interpreterOptions = JDOMExternalizerUtil.readField(element, "INTERPRETER_OPTIONS");
        interpreterPath = JDOMExternalizerUtil.readField(element, "INTERPRETER_PATH");
        workingDirectory = JDOMExternalizerUtil.readField(element, "WORKING_DIRECTORY");

        // 1.7.0 to 1.7.2 broke the run configs by defaulting to useProjectInterpreter, using field USE_PROJECT_INTERPRETER
        // we try to workaround for config saved by these versions by using another field and a smart fallback
        String useProjectInterpreterValue = JDOMExternalizerUtil.readField(element, "PROJECT_INTERPRETER");
        String oldUseProjectInterpreterValue = JDOMExternalizerUtil.readField(element, "USE_PROJECT_INTERPRETER");
        if (useProjectInterpreterValue != null) {
            useProjectInterpreter = Boolean.parseBoolean(useProjectInterpreterValue);
        } else if (StringUtils.isEmpty(interpreterPath) && oldUseProjectInterpreterValue != null) {
            // only use old "use project interpreter" setting when there's no interpreter in the run config and a configured project interpreter
            Project project = getProject();
            if (!BashProjectSettings.storedSettings(project).getProjectInterpreter().isEmpty()) {
                useProjectInterpreter = Boolean.parseBoolean(oldUseProjectInterpreterValue);
            }
        }

        String parentEnvValue = JDOMExternalizerUtil.readField(element, "PARENT_ENVS");
        if (parentEnvValue != null) {
            setPassParentEnvs(Boolean.parseBoolean(parentEnvValue));
        }

        // run config
        scriptName = JDOMExternalizerUtil.readField(element, "SCRIPT_NAME");
        setProgramParameters(JDOMExternalizerUtil.readField(element, "PARAMETERS"));
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);

        // common config
        JDOMExternalizerUtil.writeField(element, "INTERPRETER_OPTIONS", interpreterOptions);
        JDOMExternalizerUtil.writeField(element, "INTERPRETER_PATH", interpreterPath);
        JDOMExternalizerUtil.writeField(element, "PROJECT_INTERPRETER", Boolean.toString(useProjectInterpreter));
        JDOMExternalizerUtil.writeField(element, "WORKING_DIRECTORY", workingDirectory);
        JDOMExternalizerUtil.writeField(element, "PARENT_ENVS", Boolean.toString(isPassParentEnvs()));

        // run config
        JDOMExternalizerUtil.writeField(element, "SCRIPT_NAME", scriptName);
        JDOMExternalizerUtil.writeField(element, "PARAMETERS", getProgramParameters());

        //JavaRunConfigurationExtensionManager.getInstance().writeExternal(this, element);
        DefaultJDOMExternalizer.writeExternal(this, element);
        writeModule(element);
        EnvironmentVariablesComponent.writeExternal(element, getEnvs());

        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    public String getInterpreterOptions() {
        return interpreterOptions;
    }

    public void setInterpreterOptions(String interpreterOptions) {
        this.interpreterOptions = interpreterOptions;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Nullable
    public String getProgramParameters() {
        return programsParameters;
    }

    public void setProgramParameters(@Nullable String programParameters) {
        this.programsParameters = programParameters;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }
}