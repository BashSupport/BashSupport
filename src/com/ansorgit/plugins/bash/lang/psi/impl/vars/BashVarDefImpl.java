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

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashCharSequence;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashAssignmentList;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.BashElementSharedImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashVarDefStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashVarDefIndex;
import com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.google.common.collect.Sets;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.ansorgit.plugins.bash.lang.LanguageBuiltins.*;

/**
 * @author jansorg
 */
public class BashVarDefImpl extends BashBaseStubElementImpl<BashVarDefStub> implements BashVarDef, BashVar, StubBasedPsiElement<BashVarDefStub> {
    private static final TokenSet accepted = TokenSet.create(BashTokenTypes.WORD, BashTokenTypes.ASSIGNMENT_WORD);
    private static final Set<String> typeCommands = Sets.newHashSet("declare", "typeset");
    private static final Set<String> localVarDefCommands = typeCommands; // Sets.newHashSet("declare", "typeset");
    private static final Set<String> typeArrayDeclarationParams = Collections.singleton("-a");
    private static final Set<String> typeReadOnlyParams = Collections.singleton("-r");

    private final BashReference reference = new SmartVarDefReference(this);
    private final BashReference dumbReference = new DumbVarDefReference(this);

    private Boolean cachedFunctionScopeLocal;
    private String name;
    private PsiElement assignmentWord;
    private TextRange nameTextRange;

    public BashVarDefImpl(ASTNode astNode) {
        super(astNode, "Bash var def");
    }

    public BashVarDefImpl(@NotNull BashVarDefStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType, "Bash var def");
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        this.cachedFunctionScopeLocal = null;
        this.name = null;
        this.assignmentWord = null;
        this.nameTextRange = null;
    }

    public String getName() {
        BashVarDefStub stub = getStub();
        if (stub != null) {
            return stub.getName();
        }

        if (name == null) {
            PsiElement element = findAssignmentWord();
            if (element instanceof BashCharSequence) {
                name = ((BashCharSequence) element).getUnwrappedCharSequence();
            } else {
                name = element.getText();
            }
        }

        return name;
    }

    public PsiElement setName(@NotNull @NonNls String newName) throws IncorrectOperationException {
        if (!BashIdentifierUtil.isValidIdentifier(newName)) {
            throw new IncorrectOperationException("can't have an empty name");
        }

        PsiElement original = findAssignmentWord();
        PsiElement replacement = BashPsiElementFactory.createAssignmentWord(getProject(), newName);
        return BashPsiUtils.replaceElement(original, replacement);
    }

    public boolean isArray() {
        //a variable can be declared as array variable in different ways:
        // - using an array assignment a=(one two)
        // - using declare -a
        // - using typeset -a

        PsiElement assignmentValue = findAssignmentValue();

        //check if we have an array assignment part
        if (assignmentValue instanceof BashAssignmentList) {
            return true;
        }

        //check for declare -a or typeset -a
        PsiElement parentElement = getParent();
        if (parentElement instanceof BashCommand) {
            BashCommand command = (BashCommand) parentElement;

            return isCommandWithParameter(command, typeCommands, typeArrayDeclarationParams);
        }

        return false;
    }

    /**
     * Returns either a assignment word element or a array assignment word.
     *
     * @return The element which represents the left part of the assignment.
     */
    @NotNull
    public PsiElement findAssignmentWord() {
        if (assignmentWord == null) {
            PsiElement element = findChildByType(accepted);
            if (element != null) {
                assignmentWord = element;
            } else {
                //if null we probably represent a single var without assignment, i.e. the var node is nested inside of
                //a parsed var
                PsiElement firstChild = getFirstChild();
                ASTNode childNode = firstChild != null ? firstChild.getNode() : null;

                ASTNode node = childNode != null ? childNode.findChildByType(accepted) : null;
                assignmentWord = (node != null) ? node.getPsi() : firstChild;
            }
        }

        return assignmentWord;
    }

    @Nullable
    protected PsiElement findAssignmentValue() {
        PsiElement last = getLastChild();
        return last != this ? last : null;
    }

    public boolean isFunctionScopeLocal() {
        //fixme probably not ok because we look at parent elements
        if (cachedFunctionScopeLocal == null) {
            cachedFunctionScopeLocal = doIsFunctionScopeLocal();
        }

        return cachedFunctionScopeLocal;
    }

    private boolean doIsFunctionScopeLocal() {
        if (isLocalVarDef()) {
            return true;
        }

        //Although this variable has no direct local command,
        //it's still possible that an earlier usage of the local command declared this
        //variable as function local
        //
        //Solve this by using stubs and index and without a processor to prevent SOE in other processors using this function
        //filter all variable definitions which are included in the broadest function scope, all others are out of scope
        //then iterate and break if there is one def which is local and which occurs before this element

        //fixme handle injected code in functions
        BashFunctionDef functionLocalScope = BashPsiUtils.findBroadestFunctionScope(this);
        if (functionLocalScope == null) {
            return false;
        }

        final TextRange validScope = BashPsiUtils.getTextRangeInFile(functionLocalScope);

        PsiFile currentFile = getContainingFile();
        Collection<BashVarDef> allDefs = DumbService.isDumb(getProject())
                ? PsiTreeUtil.collectElementsOfType(functionLocalScope, BashVarDef.class)
                : StubIndex.getElements(BashVarDefIndex.KEY, getReferenceName(), getProject(), GlobalSearchScope.fileScope(currentFile), BashVarDef.class);

        for (BashVarDef def : allDefs) {
            if (def.isLocalVarDef() && validScope.contains(BashPsiUtils.getFileTextOffset(def))) {
                PsiElement functionScope = def.findFunctionScope();
                if (functionScope != null && functionScope.isEquivalentTo(functionLocalScope)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isLocalVarDef() {
        //check if the command is a local-var defining command, e.g. local
        final PsiElement context = getContext();
        if (context instanceof BashCommand) {
            final BashCommand parentCmd = (BashCommand) context;
            String commandName = parentCmd.getReferencedCommandName();

            //declared by "local"
            if (parentCmd.isVarDefCommand() && LanguageBuiltins.localVarDefCommands.contains(commandName)) {
                return true;
            }

            //declared by either delcare or typeset in a function block
            if (localVarDefCommands.contains(commandName) && BashPsiUtils.findNextVarDefFunctionDefScope(context) != null) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAssignmentValue() {
        return findAssignmentValue() != null;
    }

    public boolean isCommandLocal() {
        final PsiElement context = getContext();
        if (context instanceof BashCommand) {
            final BashCommand parentCmd = (BashCommand) context;
            return !parentCmd.isPureAssignment() && !parentCmd.isVarDefCommand();
        }

        return false;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState resolveState,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        if (!processor.execute(this, resolveState)) {
            return false;
        }

        return BashElementSharedImpl.walkDefinitionScope(this, processor, resolveState, lastParent, place);
    }

    public PsiElement getNameIdentifier() {
        return findAssignmentWord();
    }

    public final String getReferenceName() {
        return getName();
    }

    public PsiElement getElement() {
        return this;
    }

    @NotNull
    @Override
    public BashReference getReference() {
        return DumbService.isDumb(getProject()) ? dumbReference : reference;
    }

    @Override
    public final boolean isVarDefinition() {
        return true;
    }

    @Override
    public int getPrefixLength() {
        return 0;
    }

    @Override
    public boolean isStaticAssignmentWord() {
        PsiElement word = findAssignmentWord();
        if (word instanceof BashCharSequence) {
            return ((BashCharSequence) word).isStatic();
        }

        return true;
    }

    public BashFunctionDef findFunctionScope() {
        return PsiTreeUtil.getStubOrPsiParentOfType(this, BashFunctionDef.class);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitVarDef(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public boolean isBuiltinVar() {
        boolean isBash_v4 = BashProjectSettings.storedSettings(getProject()).isSupportBash4();

        String varName = getReferenceName();
        boolean v3_var = bashShellVars.contains(varName) || bourneShellVars.contains(varName);

        return isBash_v4 ? (v3_var || bashShellVars_v4.contains(varName)) : v3_var;
    }

    public boolean isParameterExpansion() {
        return false;
    }

    public boolean isParameterReference() {
        return false;
    }

    public boolean isArrayUse() {
        return false;
    }

    public TextRange getAssignmentNameTextRange() {
        if (nameTextRange == null) {
            PsiElement assignmentWord = findAssignmentWord();
            if (assignmentWord instanceof BashString) {
                nameTextRange = ((BashString) assignmentWord).getTextContentRange();
            } else {
                nameTextRange = TextRange.from(0, assignmentWord.getTextLength());
            }
        }

        return nameTextRange;
    }

    public boolean isReadonly() {
        PsiElement context = getParent();
        if (context instanceof BashCommand) {
            BashCommand command = (BashCommand) context;

            if (command.isInternalCommand() && LanguageBuiltins.readonlyVarDefCommands.contains(command.getReferencedCommandName())) {
                return true;
            }

            //check for declare -r or typeset -r
            if (isCommandWithParameter(command, typeCommands, typeReadOnlyParams)) {
                return true;
            }
        }

        return false;
    }

    private boolean isCommandWithParameter(BashCommand command, Set<String> validCommands, Set<String> validParams) {
        PsiElement commandElement = command.commandElement();

        if (commandElement != null && validCommands.contains(commandElement.getText())) {
            List<BashPsiElement> parameters = command.parameters();

            for (BashPsiElement param : parameters) {
                if (validParams.contains(param.getText())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    public List<PsiComment> findAttachedComment() {
        return BashPsiUtils.findDocumentationElementComments(this);
    }

}
