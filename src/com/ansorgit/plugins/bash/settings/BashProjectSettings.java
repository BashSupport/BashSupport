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

package com.ansorgit.plugins.bash.settings;

import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Set;

/**
 * Bash project level settings. In the project settings you can configure the global variables
 * registered for the current project.
 */
public class BashProjectSettings implements Serializable {
    private final Set<String> globalVariables = Sets.newConcurrentHashSet();
    private boolean autcompleteGlobalVars = true;
    private boolean supportBash4 = true;

    private boolean autocompleteBuiltinVars = false;
    private boolean autocompleteBuiltinCommands = true;
    private boolean autocompletePathCommands = true;

    private boolean globalFunctionVarDefs = false;

    private boolean formatterEnabled = false;
    private boolean evalEscapesEnabled = false;

    // true if validation logic should match against the current machine's environment
    private boolean validateWithCurrentEnv = true;

    private boolean useTerminalPlugin = false;

    private boolean variableFolding = false;

    @NotNull
    private String projectInterpreter = "";

    public static BashProjectSettings storedSettings(@NotNull Project project) {
        BashProjectSettingsComponent component = project.getComponent(BashProjectSettingsComponent.class);
        if (component == null) {
            //in the default settings there is no project available and thus no project components.
            return new BashProjectSettings();
        }

        return component.getState();
    }

    public boolean isAutocompleteBuiltinVars() {
        return autocompleteBuiltinVars;
    }

    public void setAutocompleteBuiltinVars(boolean autocompleteBuiltinVars) {
        this.autocompleteBuiltinVars = autocompleteBuiltinVars;
    }

    public boolean isAutocompleteBuiltinCommands() {
        return autocompleteBuiltinCommands;
    }

    public void setAutocompleteBuiltinCommands(boolean autocompleteBuiltinCommands) {
        this.autocompleteBuiltinCommands = autocompleteBuiltinCommands;
    }

    public void addGlobalVariable(String variableName) {
        this.globalVariables.add(variableName);
    }

    public void removeGlobalVariable(String varName) {
        this.globalVariables.remove(varName);
    }

    public Set<String> getGlobalVariables() {
        return globalVariables;
    }

    public void setGlobalVariables(Set<String> globalVariables) {
        if (this.globalVariables != globalVariables) {
            this.globalVariables.clear();
            this.globalVariables.addAll(globalVariables);
        }
    }

    public boolean isAutcompleteGlobalVars() {
        return autcompleteGlobalVars;
    }

    public void setAutcompleteGlobalVars(boolean autcompleteGlobalVars) {
        this.autcompleteGlobalVars = autcompleteGlobalVars;
    }

    public boolean isSupportBash4() {
        return supportBash4;
    }

    public void setSupportBash4(boolean supportBash4) {
        this.supportBash4 = supportBash4;
    }

    public boolean isFormatterEnabled() {
        return formatterEnabled;
    }

    public void setFormatterEnabled(boolean formatterEnabled) {
        this.formatterEnabled = formatterEnabled;
    }

    public boolean isEvalEscapesEnabled() {
        return evalEscapesEnabled;
    }

    public void setEvalEscapesEnabled(boolean evalEscapedEnabled) {
        this.evalEscapesEnabled = evalEscapedEnabled;
    }

    public boolean isAutocompletePathCommands() {
        return autocompletePathCommands;
    }

    public void setAutocompletePathCommands(boolean autocompletePathCommands) {
        this.autocompletePathCommands = autocompletePathCommands;
    }

    public boolean isGlobalFunctionVarDefs() {
        return globalFunctionVarDefs;
    }

    public void setGlobalFunctionVarDefs(boolean globalFunctionVarDefs) {
        this.globalFunctionVarDefs = globalFunctionVarDefs;
    }

    public boolean isUseTerminalPlugin() {
        return useTerminalPlugin;
    }

    public void setUseTerminalPlugin(boolean useTerminalPlugin) {
        this.useTerminalPlugin = useTerminalPlugin;
    }

    public boolean isValidateWithCurrentEnv() {
        return validateWithCurrentEnv;
    }

    public void setValidateWithCurrentEnv(boolean validateWithCurrentEnv) {
        this.validateWithCurrentEnv = validateWithCurrentEnv;
    }

    @NotNull
    public String getProjectInterpreter() {
        return projectInterpreter;
    }

    public void setProjectInterpreter(@NotNull String projectInterpreter) {
        this.projectInterpreter = projectInterpreter;
    }

    public boolean isVariableFolding() {
        return variableFolding;
    }

    public void setVariableFolding(boolean variableFolding) {
        this.variableFolding = variableFolding;
    }
}

