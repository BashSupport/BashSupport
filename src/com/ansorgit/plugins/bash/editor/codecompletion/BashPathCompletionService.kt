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

package com.ansorgit.plugins.bash.editor.codecompletion

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.SystemInfoRt
import org.apache.commons.lang.StringUtils
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.FileSystemException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Paths
import java.util.*

/**
 * Service which exposes the commands used for Bash code completion.
 *
 * @author jansorg
 */
class BashPathCompletionService() {
    companion object {
        private val LOG = Logger.getInstance("#bash.completion")

        @JvmStatic
        fun getInstance() = ServiceManager.getService(BashPathCompletionService::class.java)!!
    }

    data class CompletionItem(val filename: String, val path: String)

    private val commands: NavigableMap<String, CompletionItem> by lazy {
        val start = System.currentTimeMillis()

        val result = TreeMap<String, CompletionItem>()
        try {
            val paths = System.getenv("PATH")
            if (paths != null) {
                for (e in StringUtils.split(paths, File.pathSeparatorChar)) {
                    val trimmed = e.trim('"', File.pathSeparatorChar)
                    if (trimmed.isEmpty()) {
                        continue
                    }

                    try {
                        val path = Paths.get(trimmed)
                        if (Files.isDirectory(path)) {
                            val files = Files.find(path, 1, { f, attr -> attr.isRegularFile && Files.isExecutable(f) }, emptyArray())
                            try {
                                files.forEach {
                                    try {
                                        val fileName = it.fileName.toString()

                                        val isExecutable = when {
                                            SystemInfoRt.isWindows -> fileName.endsWith(".exe") || fileName.endsWith(".bat")
                                            else -> true
                                        }

                                        if (isExecutable) {
                                            result.put(fileName, CompletionItem(fileName, it.toString()))
                                        }
                                    } catch (e: FileSystemException) {
                                        if (LOG.isDebugEnabled) {
                                            LOG.debug("error accessing file $it")
                                        }
                                    } catch (e: UncheckedIOException) {
                                        if (LOG.isDebugEnabled) {
                                            LOG.debug("error accessing file $it")
                                        }
                                    }
                                }
                            } finally {
                                files.close()
                            }
                        }
                    } catch (ex: Exception) {
                        when (ex) {
                            is InvalidPathException, is IOException, is UncheckedIOException, is SecurityException -> LOG.debug("Invalid path detected in \$PATH element $e", ex)
                            is FileSystemException -> LOG.debug("Ignoring filesystem exception in \$PATH element $e", ex)
                            else -> LOG.error("Exception while scanning \$PATH for command names", ex)
                        }
                    }
                }
            }
            result
        } finally {
            val duration = System.currentTimeMillis() - start
            val size = result.size
            LOG.debug("bash commands loaded $size commands in $duration ms")
        }
    }

    fun findCommands(commandPrefix: String): Collection<CompletionItem> {
        val subMap = commands.subMap(commandPrefix, true, findUpperLimit(commandPrefix), true)
        return subMap.values
    }

    fun allCommands(): Collection<CompletionItem> {
        return commands.values
    }

    /**
     * Find the upper limit of the TreeSet map lookup. E.g. "git" has a upper lookup limit of "giu" (exclusive).
     *
     * @param prefix The prefix which should be used to retrieve all keys which start with this value
     * @return The key to use for the upper limit l
     */
    protected fun findUpperLimit(prefix: String): String {
        return when {
            prefix.isEmpty() -> "z"
            prefix.length == 1 -> {
                val c = prefix[0]
                if (c < 'z') Character.toString((c.toInt() + 1).toChar()) else "z"
            }
            else -> {
                //change the last character to 'z' to create the lookup range
                //if it already is 'z' then cut it off and call again with the substring
                val lastChar = prefix[prefix.length - 1]
                if (lastChar < 'z') {
                    prefix.substring(0, prefix.length - 1) + Character.toString((lastChar.toInt() + 1).toChar())
                } else {
                    findUpperLimit(prefix.substring(0, prefix.length - 1))
                }
            }
        }
    }
}