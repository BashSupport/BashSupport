/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashRunConfiguration.java, Class: BashRunConfiguration
 * Last modified: 2011-05-03 19:56
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.runner;

import com.google.common.collect.Lists;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This code is based on the intellij-batch plugin.
 *
 * @author wibotwi, jansorg
 */
public class BashRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> implements CommonBashRunConfigurationParams, BashRunConfigurationParams {
    // common config
    private String interpreterOptions = "";
    private String workingDirectory = "";
    private boolean passParentEnvs = true;
    private Map<String, String> envs = new HashMap<String, String>();
    private String interpreterPath = "";

    // run config
    private String scriptName;
    private String scriptParameters;

    protected BashRunConfiguration(RunConfigurationModule runConfigurationModule, ConfigurationFactory configurationFactory, String name) {
        super(name, runConfigurationModule, configurationFactory);
    }

    @Override
    public Collection<Module> getValidModules() {
        Module[] allModules = ModuleManager.getInstance(getProject()).getModules();
        return Lists.newArrayList(allModules);
    }

    @Override
    protected ModuleBasedConfiguration createInstance() {
        return new BashRunConfiguration(getConfigurationModule(), getFactory(), getName());
    }

    @Override
    public void createAdditionalTabComponents(AdditionalTabComponentManager manager, ProcessHandler startedProcess) {

    }

    @Override
    public boolean excludeCompileBeforeLaunchOption() {
        return true;
    }

    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new BashRunConfigurationEditor(this);
    }

    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        BashCommandLineState state = new BashCommandLineState(this, env);

        TextConsoleBuilder textConsoleBuilder = new BashTextConsoleBuilder(getProject());
        textConsoleBuilder.addFilter(new BashLineErrorFilter(getProject()));

        state.setConsoleBuilder(textConsoleBuilder);
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

        if (StringUtil.isEmptyOrSpaces(interpreterPath)) {
            throw new RuntimeConfigurationException("No interpreter path given.");
        }

        File interpreterFile = new File(interpreterPath);
        if (!interpreterFile.isFile() || !interpreterFile.canRead()) {
            throw new RuntimeConfigurationException("Interpreter path is invalid or not readable.");
        }

        if (StringUtil.isEmptyOrSpaces(scriptName)) {
            throw new RuntimeConfigurationException("No script name given.");
        }
    }

    @Override
    public boolean isGeneratedName() {
        return Comparing.equal(getName(), suggestedName());
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
        super.readExternal(element);

        // common config
        interpreterOptions = JDOMExternalizerUtil.readField(element, "INTERPRETER_OPTIONS");
        interpreterPath = JDOMExternalizerUtil.readField(element, "INTERPRETER_PATH");
        workingDirectory = JDOMExternalizerUtil.readField(element, "WORKING_DIRECTORY");

        String str = JDOMExternalizerUtil.readField(element, "PARENT_ENVS");
        if (str != null) {
            passParentEnvs = Boolean.parseBoolean(str);
        }

        EnvironmentVariablesComponent.readExternal(element, envs);

        // ???
        getConfigurationModule().readExternal(element);

        // run config
        scriptName = JDOMExternalizerUtil.readField(element, "SCRIPT_NAME");
        scriptParameters = JDOMExternalizerUtil.readField(element, "PARAMETERS");
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);

        // common config
        JDOMExternalizerUtil.writeField(element, "INTERPRETER_OPTIONS", interpreterOptions);
        JDOMExternalizerUtil.writeField(element, "INTERPRETER_PATH", interpreterPath);
        JDOMExternalizerUtil.writeField(element, "WORKING_DIRECTORY", workingDirectory);
        JDOMExternalizerUtil.writeField(element, "PARENT_ENVS", Boolean.toString(passParentEnvs));
        EnvironmentVariablesComponent.writeExternal(element, envs);

        // ???
        getConfigurationModule().writeExternal(element);

        // run config
        JDOMExternalizerUtil.writeField(element, "SCRIPT_NAME", scriptName);
        JDOMExternalizerUtil.writeField(element, "PARAMETERS", scriptParameters);
    }

    public static void copyParams(CommonBashRunConfigurationParams from, CommonBashRunConfigurationParams to) {
        to.setEnvs(new HashMap<String, String>(from.getEnvs()));
        to.setInterpreterOptions(from.getInterpreterOptions());
        to.setWorkingDirectory(from.getWorkingDirectory());
        to.setInterpreterPath(from.getInterpreterPath());
        //to.setPassParentEnvs(from.isPassParentEnvs());
    }

    public static void copyParams(BashRunConfigurationParams from, BashRunConfigurationParams to) {
        copyParams(from.getCommonParams(), to.getCommonParams());

        to.setScriptName(from.getScriptName());
        to.setScriptParameters(from.getScriptParameters());
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

    public boolean isPassParentEnvs() {
        return passParentEnvs;
    }

    public void setPassParentEnvs(boolean passParentEnvs) {
        this.passParentEnvs = passParentEnvs;
    }

    public Map<String, String> getEnvs() {
        return envs;
    }

    public void setEnvs(Map<String, String> envs) {
        this.envs = envs;
    }

    public CommonBashRunConfigurationParams getCommonParams() {
        return this;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getScriptParameters() {
        return scriptParameters;
    }

    public void setScriptParameters(String scriptParameters) {
        this.scriptParameters = scriptParameters;
    }
}