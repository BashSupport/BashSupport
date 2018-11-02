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

package com.ansorgit.plugins.bash.actions

import com.ansorgit.plugins.bash.file.BashFileTypeDetector
import com.ansorgit.plugins.bash.util.BashIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.SystemInfoRt
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiDocumentManager

/**
 * @author jansorg
 */
class ReportFileTypeAction() : AnAction("Bash: file type detection report", "", BashIcons.BASH_FILE_ICON) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) as Project
        val editor = CommonDataKeys.EDITOR.getData(e.dataContext)
        if (editor == null) {
            Messages.showInfoMessage("No open file found.", "Bash file type detection")
            return
        }

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
        if (psiFile == null) {
            Messages.showInfoMessage("No PSI file found.", "Bash file type detection")
            return
        }

        val virtualFile = psiFile.virtualFile

        val typeRegistry = FileTypeRegistry.getInstance()
        val detectedType = typeRegistry.getFileTypeByFile(virtualFile)
        val isIgnored = typeRegistry.isFileIgnored(virtualFile)
        val typeByName = typeRegistry.getFileTypeByFileName(psiFile.name)

        val psiFileType = psiFile.fileType
        val vfsFileType = virtualFile?.fileType

        val typeManager = FileTypeManagerEx.getInstanceEx()

        val initialContent = VfsUtilCore.loadText(virtualFile, 512)
        val firstLine = initialContent.lines().firstOrNull()
        val bashDetectedType = when (firstLine) {
            null -> null
            else -> BashFileTypeDetector.detect(virtualFile, firstLine)
        }

        val report = """
            Please report an issue at https://github.com/BashSupport/BashSupport/issues
            if the Bash file isn't properly displayed. You can select and copy the information below.

            File name: ${virtualFile.path}
            File extension: ${virtualFile.extension}
            First line: $firstLine

            Attached PSI file type: $psiFileType
            Attached VFS file type: $vfsFileType
            Detected file type: ${detectedType}
            Ignored: ${isIgnored}
            Type by filename: ${typeByName}

            Ignored files: ${typeManager.ignoredFilesList}
            Detected by bash detector: ${bashDetectedType}

            IDE: ${ApplicationInfo.getInstance().build}
            OS: ${SystemInfoRt.OS_NAME}
            OS version: ${SystemInfoRt.OS_VERSION}
            File system case sensitive: ${SystemInfoRt.isFileSystemCaseSensitive}
        """.trimIndent()

        Messages.showInfoMessage(report, "Bash file type detection")
    }
}