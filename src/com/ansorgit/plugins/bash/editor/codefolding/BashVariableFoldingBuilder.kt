package com.ansorgit.plugins.bash.editor.codefolding

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes
import com.ansorgit.plugins.bash.lang.psi.api.BashReference
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarDefImpl
import com.ansorgit.plugins.bash.lang.psi.impl.vars.FoldingSmartVarReference
import com.ansorgit.plugins.bash.lang.psi.stubs.elements.BashVarElementType
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.ContainerUtil.newArrayList

/**
 * Folding for variables in bash script.
 *
 * For example this code:
 *     PWD=/tmp/daily-nsnam/foo
 *     say "-d forcing PWD = $PWD"
 *
 * getting like:
 *     PWD=/tmp/daily-nsnam/foo
 *     say "-d forcing PWD = /tmp/daily-nsnam/foo"
 *
 * @author Nosov Aleksandr <nosovae.dev@gmail.com>
 */
class BashVariableFoldingBuilder : FoldingBuilderEx(), DumbAware {

    private val DEFAULT_DEPTH_OF_FOLDING = 1

    override fun getPlaceholderText(node: ASTNode) = getPlaceholderText(node, DEFAULT_DEPTH_OF_FOLDING)

    private fun getPlaceholderText(node: ASTNode, depth: Int): String {
        val bashVar = node.psi as BashVar

        val reference = FoldingSmartVarReference(bashVar.reference, true).resolve()
        if (reference != null && reference is BashVarDefImpl) {
            val value = reference.findAssignmentValue()
            if (value != null) {
                return computePlaceholderText(value.node, depth)
            }
        }
        return node.text
    }

    private fun computePlaceholderText(valueNode: ASTNode, currentDepth: Int): String {
        return valueNode.getChildren(null)
                .filter { it.elementType !== BashTokenTypes.STRING_BEGIN }
                .filter { it.elementType !== BashTokenTypes.STRING_END }
                .map {
                    if (it.elementType is BashVarElementType) {
                        if (currentDepth <= 0) {
                            return@map it.text
                        }
                        return@map getPlaceholderText(it, currentDepth - 1)
                    }
                    it.text
                }.joinToString(separator = "")
    }


    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = newArrayList<FoldingDescriptor>()

        val foldingBlocks = newArrayList<PsiElement>()
        PsiTreeUtil.processElements(root, PsiElementProcessor.CollectFilteredElements(
                { psiElement -> psiElement is BashVar && psiElement !is BashVarDef }, foldingBlocks
        ))

        foldingBlocks.forEach { psiElement ->
            if (FoldingSmartVarReference(psiElement.reference as BashReference?, true).resolve() != null) {
                descriptors.add(FoldingDescriptor(psiElement, psiElement.textRange))
            }
        }

        return descriptors.toTypedArray()
    }

    override fun isCollapsedByDefault(node: ASTNode) = false
}