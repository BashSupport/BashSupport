package com.ansorgit.plugins.bash.editor.codecompletion

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator

/**
 * Loads the code completion data in a background thread.
 *
 * @author jansorg
 */
class PreloadPathCompletionActivity : PreloadingActivity() {
    override fun preload(progress: ProgressIndicator) {
        // make sure that the commands are loaded
        BashPathCompletionService.getInstance().findCommands("test")
    }
}