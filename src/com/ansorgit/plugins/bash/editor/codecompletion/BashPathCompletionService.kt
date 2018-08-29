package com.ansorgit.plugins.bash.editor.codecompletion

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import org.apache.commons.lang.StringUtils
import java.io.File
import java.nio.file.Files
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
        fun getInstance() = ServiceManager.getService(BashPathCompletionService::class.java)
    }

    data class CompletionItem(val filename: String, val path: String)

    private val commands: NavigableMap<String, CompletionItem> by lazy {
        val start = System.currentTimeMillis()

        val result = TreeMap<String, CompletionItem>()
        try {
            val paths = System.getenv("PATH")
            if (paths != null) {
                for (e in StringUtils.split(paths, File.pathSeparatorChar)) {
                    val path = Paths.get(e)
                    if (Files.isDirectory(path)) {
                        Files.find(path, 1, { file, attrs -> Files.isExecutable(file) && Files.isRegularFile(file) }, emptyArray()).forEach {
                            val filename = it.fileName.toString()
                            result.put(filename, CompletionItem(filename, it.toString()))
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