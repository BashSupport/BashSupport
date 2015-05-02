/**
 * ****************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashIcons.java, Class: BashIcons
 * Last modified: 2011-04-30 16:33
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */

package com.ansorgit.plugins.bash.util;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Contains the paths for the various Bash icons.
 *
 * @author Joachim Ansorg, mail@ansorg-it.com.
 */
public interface BashIcons {

    Icon BASH_LARGE_ICON = IconLoader.getIcon("/icons/bash-64.png");

    Icon BASH_FILE_ICON = IconLoader.getIcon("/icons/bash.png");

    Icon FUNCTION_DEF_ICON = IconLoader.findIcon("/icons/function-16.png");

    Icon GLOBAL_VAR_ICON = IconLoader.findIcon("/icons/global-var-16.png");

    Icon BASH_VAR_ICON = IconLoader.findIcon("/icons/bash-var-16.png");

    Icon BOURNE_VAR_ICON = IconLoader.findIcon("/icons/bash-var-16.png");
}
