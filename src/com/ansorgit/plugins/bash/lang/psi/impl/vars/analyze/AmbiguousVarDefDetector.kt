package com.ansorgit.plugins.bash.lang.psi.impl.vars.analyze

import com.ansorgit.plugins.bash.lang.psi.api.shell.BashCasePatternListElement
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashIf
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Interface and some implementations of detector of ambiguous variable definition.
 * 
 * @author Nosov Aleksandr <nosovae.dev@gmail.com>
 */
interface AmbiguousVarDefDetector {
    fun detect(varDefinition: PsiElement, varUse: PsiElement): Boolean
}

/**
 * Detect ambiguous variable definition due to IF condition.
 *
 * @author Nosov Aleksandr <nosovae.dev@gmail.com>
 */
class IfBlockDetector : AmbiguousVarDefDetector {
    override fun detect(varDefinition: PsiElement, varUse: PsiElement): Boolean {
        val varDefParentBlock = varDefinition.findParentIfCondition()
        if (varDefParentBlock != null) {
            val varParentBlock = varUse.findParentIfCondition()
            return !(varParentBlock != null && varParentBlock == varDefParentBlock)
        }
        return false
    }
}

/**
 * Detect ambiguous variable definition due to CASE condition.
 *
 * @author Nosov Aleksandr <nosovae.dev@gmail.com>
 */
class CaseBlockDetector : AmbiguousVarDefDetector {
    override fun detect(varDefinition: PsiElement, varUse: PsiElement): Boolean {
        val varDefParentBlock = varDefinition.findParentCaseCondition()
        if (varDefParentBlock != null) {
            val varParentBlock = varUse.findParentCaseCondition()
            return !(varParentBlock != null && varParentBlock == varDefParentBlock)
        }
        return false
    }
}

private fun PsiElement.findParentIfCondition(): PsiElement? = PsiTreeUtil.getParentOfType(this, BashIf::class.java)
private fun PsiElement.findParentCaseCondition(): PsiElement? = PsiTreeUtil.getParentOfType(this, BashCasePatternListElement::class.java)
