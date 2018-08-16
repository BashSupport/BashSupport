package com.ansorgit.plugins.bash.lang.psi.impl.vars

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef
import com.ansorgit.plugins.bash.lang.psi.impl.vars.analyze.AmbiguousVarDefDetector
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState

/**
 * Processor for resolve references for correct folding of variables.
 *
 * @see com.ansorgit.plugins.bash.editor.codefolding.BashVariableFoldingBuilder
 * @author Nosov Aleksandr <nosovae.dev@gmail.com>
 */
class FoldingBashVarProcessor(val startElement: BashVar, private val variableName: String,
                              preferNeighbourhood: Boolean,
                              private val ambiguousVarDefDetectors: List<AmbiguousVarDefDetector>) :
        BashVarProcessor(startElement, variableName, true, false, preferNeighbourhood) {

    private val checkLocalness = true

    private var ignoreGlobals = false
    private var isAmbiguousDefinition = false

    override fun execute(varDef: PsiElement, resolveState: ResolveState): Boolean {
        if (varDef is BashVarDef) {

            if (variableName != varDef.name || startElement == varDef || startElement == varDef) {
                //proceed with the search
                return true
            }

            //we have the same name, so it's a possible hit
            //now check the scope
            val localVarDef = varDef.isFunctionScopeLocal
            val isValid = if (checkLocalness && localVarDef)
                isValidLocalDefinition(varDef, resolveState)
            else
                isValidDefinition(varDef, resolveState)

            //if we found a valid local variable definition we must ignore all (otherwise matching) global variable definitions
            ignoreGlobals = ignoreGlobals || isValid && checkLocalness && localVarDef

            if (isValid) {
                isAmbiguousDefinition = ambiguousVarDefDetectors.any { it.detect(varDef, startElement) }
                storeResult(varDef, BashPsiUtils.blockNestingLevel(varDef))

                if (!varDef.isLocalVarDef) {
                    globalVariables.add(varDef)
                }

                return false
            }
        }

        return true
    }

    override fun prepareResults() {
        if (isAmbiguousDefinition) {
            results!!.clear()
            return
        }

        super.prepareResults()
    }
}

