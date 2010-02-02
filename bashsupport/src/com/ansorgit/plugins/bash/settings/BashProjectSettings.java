/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashProjectSettings.java, Class: BashProjectSettings
 * Last modified: 2009-12-04
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

package com.ansorgit.plugins.bash.settings;

import com.intellij.openapi.project.Project;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Bash project level settings. In the project settings you can configure the global variables
 * registered for the current project.
 * <p/>
 * User: jansorg
 * Date: Oct 30, 2009
 * Time: 9:14:18 PM
 */
public class BashProjectSettings implements Serializable {
    private Set<String> globalVariables = new HashSet<String>();
    private boolean autcompleteGlobalVars = true;
    private boolean supportBash4 = false;

    public static BashProjectSettings storedSettings(Project project) {
        return project.getComponent(BashProjectSettingsComponent.class).getState();
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
        this.globalVariables = globalVariables;
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
}
