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

package com.ansorgit.plugins.bash.util

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.IconLoader
import com.intellij.util.PlatformIcons
import javax.swing.Icon

/**
 * Providese the icons which are available in the BashSupport plugin.
 *
 * @author jansorg
 */
object BashIcons {
    private val LOG = Logger.getInstance("#bash.icons")

    @JvmField
    val BASH_FILE_ICON = load("/icons/fileTypes/BashFileIcon.png")

    // icon used to render regular script functions
    @JvmField
    val FUNCTION_ICON = load("/icons/fileTypes/BashFileIcon.png")

    // icon used to render regular script variables in code completion
    @JvmField
    val VAR_ICON = load("/icons/fileTypes/BashFileIcon.png")

    // icon used to render BashSupport's global variables stored in settings
    @JvmField
    val GLOBAL_VAR_ICON = load("/icons/global-var-16.png")

    // icon used to render Bash's built-in variables
    @JvmField
    val BASH_VAR_ICON = load("/icons/bash-var-16.png")

    // icon used to render Bash's built-in variables of version 4 or later
    @JvmField
    val BOURNE_VAR_ICON = load("/icons/bash-var-16.png")

    // https://github.com/BashSupport/BashSupport/issues/611
    // a concurrent modification exception is raised when we load the icon.
    // we assume that this is caused by a modification of the icon patchers by another plugin at the same time
    // as BashSupport's classes are initialized (and call the IconLoader's findIcon methods)
    // we use "findIcon(URL)" which isn't using the icon patcher to work around this issue
    private fun load(resourcePath: String): Icon {
        val icon = try {
            val url = BashIcons::class.java.getResource(resourcePath)
            IconLoader.findIcon(url, true)
        } catch (e: Exception) {
            LOG.warn("Error while loading icon $resourcePath", e)
            null
        }

        return icon ?: PlatformIcons.FILE_ICON
    }
}
