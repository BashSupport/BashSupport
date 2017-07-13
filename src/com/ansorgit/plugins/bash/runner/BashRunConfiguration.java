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

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configuration.AbstractRunConfiguration;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.ProgramParametersUtil;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.search.GlobalSearchScope;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;

/**
 * This code is based on the intellij-batch plugin.
 *
 * @author wibotwi, jansorg
 */
class BashRunConfiguration extends AbstractRunConfiguration implements BashRunConfigurationParams, RunConfigurationWithSuppressedDefaultDebugAction {
    private String interpreterOptions = "";
    private String workingDirectory = "";
    private String interpreterPath = "";
    private String scriptName;
    private String programsParameters;

    BashRunConfiguration(RunConfigurationModule runConfigurationModule, ConfigurationFactory configurationFactory, String name) {
        super(name, runConfigurationModule, configurationFactory);
    }

    @Override
    public Collection<Module> getValidModules() {
        return getAllModules();
    }

    @Override
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
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        Module module = getConfigurationModule().getModule();
        if (module != null) {
            //a missing module will cause a NPE in the check method
            ProgramParametersUtil.checkWorkingDirectoryExist(this, getProject(), module);
        }

        if (StringUtil.isEmptyOrSpaces(interpreterPath)) {
            throw new RuntimeConfigurationException("No interpreter path given.");
        }

        File interpreterFile = new File(interpreterPath);
        if (!interpreterFile.isFile() || !interpreterFile.canRead()) {
            throw new RuntimeConfigurationException("Interpreter path is invalid or not readable.");
        }

        if (StringUtil.isEmptyOrSpaces(scriptName)) {
            throw new RuntimeConfigurationException("Script name not given.");
        }
    }

    @Override
    public String suggestedName() {
        String name = (new File(scriptName)).getName();

        int ind = name.lastIndexOf('.');
        if (ind != -1) {
            return name.substring(0, ind);
        }
        return name;
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

    @Nullable
    @Override
    public GlobalSearchScope getSearchScope() {
        return GlobalSearchScope.allScope(getProject());
    }
}