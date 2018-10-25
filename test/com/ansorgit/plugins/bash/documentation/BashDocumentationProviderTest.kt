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

package com.ansorgit.plugins.bash.documentation

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase
import com.ansorgit.plugins.bash.file.BashFileType
import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.junit.Assert
import org.junit.Test

class BashDocumentationProviderTest : LightBashCodeInsightFixtureTestCase() {
    @Test
    @Throws(Exception::class)
    fun testFunctionComment() {
        assertCaretElementDocumentation("Documentation for function a<br/>")
    }

    @Test
    @Throws(Exception::class)
    fun testVarDefComment() {
        assertCaretElementDocumentation("Documentation for varDef a<br/>")
    }

    @Test
    @Throws(Exception::class)
    fun testWhileKeyword() {
        val doc = findDocumentationForCaret()
        Assert.assertNotNull(doc)
    }

    @Test
    @Throws(Exception::class)
    fun testIfKeyword() {
        Assert.assertNotNull("Expected documentation for the internal command", findDocumentationForCaret())
    }

    @Test
    fun testExternalCommand1() {
        Assert.assertNotNull("Expected documentation for the external command", findDocumentationForCaret())
    }

    @Test
    fun testExternalCommand2() {
        Assert.assertNotNull(findDocumentationForCaret())
    }

    @Test
    fun testInfoCommandDoc() {
        //must be run on a command which doesn't have a pre-computed html file
        Assert.assertNotNull("Expected documentation content", findDocumentationForCaret())
    }

    @Test
    fun testInfoCommandDocEndOffset() {
        //must be run on a command which doesn't have a pre-computed html file
        Assert.assertNotNull("Expected documentation content", findDocumentationForCaret())
    }

    @Test
    fun testCommands() {
        assertValidDocumentation("curl<caret>")
        assertValidDocumentation("curl<caret>\n")
        assertValidDocumentation("curl<caret> && echo")
        assertValidDocumentation("$(curl<caret>)")
        assertValidDocumentation("\"$(curl<caret>)\"")

        assertValidDocumentation("<caret>echo")
        assertValidDocumentation("echo<caret>")

        assertNoValidDocumentation("curl a<caret>")
        assertNoValidDocumentation("$(curl a<caret>)")
        assertNoValidDocumentation("\"$(curl a<caret>)\"")
    }

    private fun assertValidDocumentation(content: String) {
        val doc = findDocumentationForContent(content)
        Assert.assertNotNull("Expected documentation", doc)
    }

    private fun assertNoValidDocumentation(content: String) {
        val doc = findDocumentationForContent(content)
        Assert.assertNull(doc)
    }

    private fun assertCaretElementDocumentation(expectedDocumentation: String) {
        val doc = findDocumentationForCaret()

        Assert.assertEquals("Expected comment to be returned as doc", expectedDocumentation, doc)
    }

    private fun findDocumentationForCaret(): String? {
        val originalElement = configurePsiAtCaret()
        return findDocumentation(originalElement)
    }

    private fun findDocumentationForContent(content: String): String? {
        val file = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, content)
        val psi = getContextElement(myFixture.editor, file)
        return findDocumentation(psi)
    }

    private fun findDocumentation(originalElement: PsiElement?): String? {
        val manager = DocumentationManager.getInstance(project)
        val targetElement = manager.findTargetElement(myFixture.editor, myFixture.file, originalElement)
        if (targetElement is PsiFile || targetElement == null) {
            return null
        }

        val provider = DocumentationManager.getProviderFromElement(targetElement, originalElement)
        val doc = provider.generateDoc(targetElement, originalElement)
        return if ("No documentation found." == doc) null else doc
    }

    override fun getBasePath(): String {
        return "editor/documentation/documentationProvider"
    }

    // same as in DocumentationManager
    private fun getContextElement(editor: Editor, file: PsiFile?): PsiElement? {
        return file?.findElementAt(editor.caretModel.offset)
    }
}