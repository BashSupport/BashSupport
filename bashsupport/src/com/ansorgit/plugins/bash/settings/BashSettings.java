/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSettings.java, Class: BashSettings
 * Last modified: 2010-01-29
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

import com.ansorgit.plugins.bash.lang.BashLoader;

import java.io.Serializable;

/**
 * Date: 12.05.2009
 * Time: 18:49:01
 *
 * @author Joachim Ansorg
 */
public class BashSettings implements Serializable {
    private boolean loadEmptyExtensions = false;
    private boolean guessByContent = false;
    private boolean autocompleteBuiltinVars = false;
    private boolean autocompleteBuiltinCommands = true;

    public static BashSettings storedSettings() {
        return BashLoader.getInstance().getSettingsComponent().getState();
    }

    public boolean isLoadEmptyExtensions() {
        return loadEmptyExtensions;
    }

    public void setLoadEmptyExtensions(boolean loadEmptyExtensions) {
        this.loadEmptyExtensions = loadEmptyExtensions;
    }

    public boolean isGuessByContent() {
        return guessByContent;
    }

    public void setGuessByContent(boolean guessByContent) {
        this.guessByContent = guessByContent;
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
}
