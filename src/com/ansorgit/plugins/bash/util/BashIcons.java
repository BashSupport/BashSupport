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

package com.ansorgit.plugins.bash.util;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Contains the paths for the various Bash icons.
 *
 * @author jansorg, mail@ansorg-it.com.
 */
public interface BashIcons {
    Icon BASH_FILE_ICON = IconLoader.findIcon("/icons/fileTypes/BashFileIcon.png", BashIcons.class, false, false);

    // icon used to render regular script functions
    Icon FUNCTION_ICON = IconLoader.findIcon("/icons/fileTypes/BashFileIcon.png", BashIcons.class, false, false);

    // icon used to render regular script variables in code completion
    Icon VAR_ICON = IconLoader.findIcon("/icons/fileTypes/BashFileIcon.png", BashIcons.class, false, false);

    // icon used to render BashSupport's global variables stored in settings
    Icon GLOBAL_VAR_ICON = IconLoader.findIcon("/icons/global-var-16.png", BashIcons.class, false, false);

    // icon used to render Bash's built-in variables
    Icon BASH_VAR_ICON = IconLoader.findIcon("/icons/bash-var-16.png", BashIcons.class, false, false);

    // icon used to render Bash's built-in variables of version 4 or later
    Icon BOURNE_VAR_ICON = IconLoader.findIcon("/icons/bash-var-16.png", BashIcons.class, false, false);
}
