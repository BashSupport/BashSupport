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

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.SystemInfoRt
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import org.apache.commons.lang.StringUtils

/**
 * This is the bash documentation provider. A documentation provider is called
 * for PsiElements which implement the interface PsiReference.
 * <br></br>
 *
 * @author jansorg
 */
class BashDocumentationProvider : AbstractDocumentationProvider() {
    private val sourceList = mutableListOf(PsiElementCommentSource(), BashKeywordDocSource(), InternalCommandDocumentation(), ManpageDocSource())

    init {
        if (!SystemInfoRt.isWindows) {
            //there is currently no support for the cygwin info command
            sourceList.add(CachingDocumentationSource(SystemInfopageDocSource()))
        }
    }

    override fun getCustomDocumentationElement(editor: Editor, file: PsiFile, contextElement: PsiElement?): PsiElement? {
        if (contextElement is BashVar) {
            return null
        }

        // pick leaf node
        val offset = editor.caretModel.offset
        for (o in listOf(offset, offset - 1)) {
            val psi = file.findElementAt(o)
            if (psi != null) {
                val parent = BashPsiUtils.findEquivalentParent(psi, BashElementTypes.GENERIC_COMMAND_ELEMENT)
                if (parent != null) {
                    return parent
                }
            }
        }

        return null
    }

    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? = null

    override fun getUrlFor(element: PsiElement?, originalElement: PsiElement?): List<String>? {
        for (source in sourceList) {
            val url = source.documentationUrl(element, originalElement)
            if (url != null && StringUtils.stripToNull(url) != null) {
                return listOf(url)
            }
        }

        return null
    }

    /**
     * Generates the documentation for a given PsiElement. The original
     * element is the token the caret was on at the time the documentation
     * was called.
     *
     * @param element         The element for which the documentation has been requested.
     * @param originalElement The element the caret is on
     * @return The HTML formatted String which contains the documentation.
     */
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        for (source in sourceList) {
            val doc = source.documentation(element, originalElement)
            if (StringUtils.stripToNull(doc) != null) {
                return doc
            }
        }

        return null
    }

    override fun getDocumentationElementForLookupItem(psiManager: PsiManager?, `object`: Any?, element: PsiElement?): PsiElement? = element

    override fun getDocumentationElementForLink(psiManager: PsiManager?, link: String?, context: PsiElement?): PsiElement? = context
}
