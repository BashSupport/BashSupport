package com.ansorgit.plugins.bash.editor.codefolding

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes
import com.ansorgit.plugins.bash.lang.psi.BashVisitor
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarDefImpl
import com.ansorgit.plugins.bash.lang.psi.stubs.elements.BashVarElementType
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils
import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil
import com.ansorgit.plugins.bash.settings.BashProjectSettings
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
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
    companion object {
        private val skippedTextTokens = TokenSet.create(BashTokenTypes.STRING_BEGIN, BashTokenTypes.STRING_END)
        private val DEFAULT_DEPTH_OF_FOLDING = 1
    }

    override fun getPlaceholderText(node: ASTNode) = computePlaceholderText(node, DEFAULT_DEPTH_OF_FOLDING)

    private fun computePlaceholderText(node: ASTNode, depth: Int): String {
        val bashVar = node.psi as? BashVar

        var replacement = node.text
        if (depth > 0 && bashVar != null && BashResolveUtil.hasStaticVarDefPath(bashVar)) {
            val varDefValue = (bashVar.neighborhoodReference?.resolve() as? BashVarDefImpl)?.findAssignmentValue()
            if (varDefValue != null) {
                replacement = nodePlaceholderText(varDefValue.node, depth)
            }
        }

        return replacement
    }

    private fun nodePlaceholderText(valueNode: ASTNode, currentDepth: Int): String {
        return valueNode.getChildren(null)
                .filterNot { skippedTextTokens.contains(it.elementType) }
                .map {
                    if (it.elementType is BashVarElementType && currentDepth > 0) {
                        computePlaceholderText(it, currentDepth - 1)
                    } else {
                        it.text
                    }
                }.joinToString(separator = "")
    }


    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        if (DumbService.isDumb(root.project) || !BashProjectSettings.storedSettings(root.project).isVariableFolding) {
            return emptyArray()
        }

        val descriptors = newArrayList<FoldingDescriptor>()
        BashPsiUtils.visitRecursively(root, object : BashVisitor() {
            override fun visitVarUse(bashVar: BashVar) {
                if (BashResolveUtil.hasStaticVarDefPath(bashVar)) {
                    descriptors.add(FoldingDescriptor(bashVar, bashVar.textRange))
                }
            }
        })

        return descriptors.toTypedArray()
    }

    override fun isCollapsedByDefault(node: ASTNode) = false
}