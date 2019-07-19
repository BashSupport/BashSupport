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

package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.Keys;
import com.ansorgit.plugins.bash.lang.psi.util.BashAbstractProcessor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.google.common.collect.Sets;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author jansorg
 */
public class BashVarProcessor extends BashAbstractProcessor implements Keys {
    private final BashVar startElement;
    private final BashFunctionDef startElementScope;
    private final boolean collectAllDefinitions;
    private final boolean checkLocalness;
    private final String varName;
    private boolean ignoreGlobals;
    private final boolean functionVarDefsAreGlobal;
    private final int startElementTextOffset;
    private final Set<PsiElement> globalVariables = Sets.newLinkedHashSet();

    public BashVarProcessor(BashVar startElement, String variableName, boolean checkLocalness, boolean preferNeighbourhood, boolean collectAllDefinitions) {
        super(preferNeighbourhood);

        this.startElement = startElement;
        this.checkLocalness = checkLocalness;
        this.varName = variableName;
        this.startElementScope = BashPsiUtils.findNextVarDefFunctionDefScope(startElement);
        this.collectAllDefinitions = collectAllDefinitions;

        this.ignoreGlobals = false;
        this.functionVarDefsAreGlobal = BashProjectSettings.storedSettings(startElement.getProject()).isGlobalFunctionVarDefs();
        this.startElementTextOffset = BashPsiUtils.getFileTextOffset(startElement);
    }

    public boolean execute(@NotNull PsiElement psiElement, @NotNull ResolveState resolveState) {
        ProgressManager.checkCanceled();

        if (psiElement instanceof BashVarDef) {
            BashVarDef varDef = (BashVarDef) psiElement;

            if (!varName.equals(varDef.getName()) || startElement == psiElement || startElement.equals(psiElement)) {
                //proceed with the search
                return true;
            }

            PsiElement includeCommand = resolveState.get(resolvingIncludeCommand);

            // we have the same name, so it's a possible hit -> now check the scope
            boolean localVarDef = varDef.isFunctionScopeLocal();
            boolean isValid = checkLocalness && localVarDef
                    ? isValidLocalDefinition(varDef)
                    : isValidDefinition(varDef, resolveState);

            // if we found a valid local variable definition we must ignore all (otherwise matching) global variable definitions
            ignoreGlobals = ignoreGlobals || (isValid && checkLocalness && localVarDef);

            if (isValid) {
                PsiElement defAnchor = includeCommand != null ? includeCommand : varDef;

                storeResult(varDef, BashPsiUtils.blockNestingLevel(defAnchor), includeCommand);

                if (!localVarDef) {
                    globalVariables.add(varDef);
                }

                return collectAllDefinitions;
            }
        }

        return true;
    }

    protected boolean isValidDefinition(BashVarDef varDef, ResolveState resolveState) {
        if (varDef.isCommandLocal()) {
            return false;
        }

        if (!varDef.isStaticAssignmentWord()) {
            return false;
        }

        //if the start element is a variable definition and is local then the new definition is invalid
        if (startElement.isVarDefinition() && ((BashVarDef)startElement).isLocalVarDef()) {
            return false;
        }

        BashFunctionDef varDefScope = BashPsiUtils.findNextVarDefFunctionDefScope(varDef);
        if (ignoreGlobals && varDefScope == null) {
            return false;
        }

        //first case: the definition is before the start element -> the definition is valid
        //second case: the definition is after the start element:
        //  - if startElement and varDef do NOT share a common scope -> varDef is only valid if it's inside of a function definition, i.e. global
        //  - if startElement and varDef share a scope which different from the PsiFile -> valid if the startElement is inside of a function def
        //this check is only valid if both elements are in the same file

        boolean sameFiles = BashPsiUtils.findFileContext(startElement).isEquivalentTo(BashPsiUtils.findFileContext(varDef));
        if (sameFiles) {
            int textOffsetVarDef = BashPsiUtils.getFileTextOffset(varDef);
            if (startElementTextOffset >= textOffsetVarDef) {
                return isDefinitionOffsetValid(varDefScope);
            }

            //the found varDef is AFTER the startElement

            if (varDefScope == null) {
                //if varDef is on global level, then it is only valid if startElement is inside of a function definition
                return startElementScope != null;
            }

            //varDef has a valid function def scope AND comes after the start element
            //in this case it is only valid if start element is in a nested function definition inside of varDefScope
            // The found variable definition is defined in a function. If the settings is enabled, i.e. less strict checking, then the variable definition is valid
            // if two var defs are compared then the varDef candidate (which occurs later in the file) is not a possible definition
            if (functionVarDefsAreGlobal && startElementScope != null && !startElement.isVarDefinition() && !varDefScope.equals(startElementScope)) {
                return true;
            }

            if (startElementScope != null) {
                return PsiTreeUtil.isAncestor(varDefScope, startElementScope, true);
            }
        } else {
            // this is the command in the original file which starts the chain of inclusions
            PsiElement includeCommand = resolveState.get(resolvingIncludeCommand);
            if (includeCommand == null) {
                return false;
            }

            BashFunctionDef includeCommandScope = BashPsiUtils.findNextVarDefFunctionDefScope(includeCommand);

            // now check the offset of the include command
            int definitionOffset = BashPsiUtils.getFileTextOffset(startElement);
            int includeCommandOffset = BashPsiUtils.getFileTextOffset(includeCommand);
            if (definitionOffset >= includeCommandOffset) {
                return isDefinitionOffsetValid(includeCommandScope);
            }

            // the include command comes AFTER the start element
            if (includeCommandScope == null) {
                return BashPsiUtils.findNextVarDefFunctionDefScope(includeCommand) != null;
            }

            if (startElementScope != null) {
                return PsiTreeUtil.isAncestor(varDefScope, includeCommandScope, true);
            }
        }

        return false;
    }

    private boolean isDefinitionOffsetValid(BashFunctionDef varDefScope) {
        //the var def is only valid if the varDef is NOT inside of a nested function (our rule is: more global is better)

        if (startElementScope == null) {
            //if the start element is on global level, then the var def has to be global, too, if the start element is a var def, also
            //if it it just a variabale which references the definition, then varDef is a valid definition for it
            return varDefScope == null || !startElement.isVarDefinition();
        }

        return varDefScope == null || varDefScope.equals(startElementScope) || !PsiTreeUtil.isAncestor(startElementScope, varDefScope, true);
    }

    /**
     * A local var def is a valid definition for our start element if it's scope contains the start
     * element.
     * <br>
     * Also, the checked variable definition has to appear before the start element.
     *
     * @param varDef       The variable definition in question
     * @return True if varDef is a valid local definition for startElement
     */
    protected boolean isValidLocalDefinition(BashVarDef varDef) {
        boolean validScope = PsiTreeUtil.isAncestor(BashPsiUtils.findEnclosingBlock(varDef), startElement, false);

        //fixme: this is not entirely true, think of a function with a var redefinition of a local variable of the inner functions
        //context (i.e. the outer function)
        //for now, this is ok
        return validScope && BashPsiUtils.getFileTextOffset(varDef) < BashPsiUtils.getFileTextOffset(startElement);
    }

    @Override
    public void prepareResults() {
        if (ignoreGlobals) {
            for (PsiElement globalVar : globalVariables) {
                removeResult(globalVar);
            }
        }
    }

    public <T> T getHint(@NotNull Key<T> key) {
        return null;
    }

    protected Set<PsiElement> getGlobalVariables() {
        return globalVariables;
    }
}
