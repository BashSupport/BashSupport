/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashIcons.java, Class: BashIcons
 * Last modified: 2010-03-15
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

package com.ansorgit.plugins.bash.util;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;

/**
 * Contains the paths for the various Bash icons.
 *
 * @author Joachim Ansorg, mail@ansorg-it.com.
 */
public interface BashIcons {
    @NonNls
    final String DATA_PATH = "/icons/";

    final Icon BASH_LARGE_ICON = IconLoader.findIcon(DATA_PATH + "bash-64.png");

    final Icon BASH_FILE_ICON = IconLoader.findIcon(DATA_PATH + "bash-16.png");

    final Icon FUNCTION_DEF_ICON = IconLoader.findIcon(DATA_PATH + "function-16.png");

    final Icon GLOBAL_VAR_ICON = IconLoader.findIcon(DATA_PATH + "global-var-16.png");

    final Icon BASH_VAR_ICON = IconLoader.findIcon(DATA_PATH + "bash-var-16.png");

    final Icon BOURNE_VAR_ICON = IconLoader.findIcon(DATA_PATH + "bash-var-16.png");
}
