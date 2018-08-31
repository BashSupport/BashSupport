package com.ansorgit.plugins.bash.util

import com.intellij.openapi.util.SystemInfo

/**
 * @author jansorg
 */
object BashFiles {
    /**
     * Replaces '~' and '$HOME'
     */
    @JvmStatic
    fun replaceHomePlaceholders(value: String): String {
        var result = value
        if (result.startsWith('~')) {
            result = userHomeDir() + value.substring(1)
        }
        return result.replace("\$HOME", userHomeDir(), false)
    }

    @JvmStatic
    fun containsSupportedPlaceholders(value: String) = value.startsWith("~") || value.contains("\$HOME")

    @JvmStatic
    fun userHomeDir(): String {
        return if (SystemInfo.isWindows) {
            System.getenv("USERPROFILE")
        } else {
            System.getenv("HOME")
        }
    };
}