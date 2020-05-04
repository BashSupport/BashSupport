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

package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.jetbrains.PsiScopesUtil;
import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashRedirectList;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.Keys;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashCommandStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashCommandStubBase;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class AbstractBashCommand<T extends BashCommandStubBase> extends BashBaseStubElementImpl<T> implements BashCommand, Keys {
    public AbstractBashCommand(ASTNode astNode, String name) {
        super(astNode, name);
    }

    public AbstractBashCommand(T stub, IStubElementType nodeType, String name) {
        super(stub, nodeType, name);
    }

    @NotNull

    @Override
    public BashFile getContainingFile() {
        return (BashFile)super.getContainingFile();
    }

    public boolean isGenericCommand() {
        T stub = getStub();
        if (stub != null) {
            return stub.isGenericCommand();
        }

        return commandElementNode() != null;
    }

    public boolean isFunctionCall() {
        if (!isGenericCommand()) {
            return false;
        }

        for (PsiReference reference : getReferences()) {
            PsiElement target = reference.resolve();
            if (target instanceof BashFunctionDef) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInternalCommand(boolean bash4) {
        T stub = getStub();
        if (stub != null) {
            return stub.isInternalCommand(bash4);
        }

        boolean isBash3 = false;
        boolean isBash4 = false;
        if (isGenericCommand()) {
            String commandText = getReferencedCommandName();
            isBash3 = LanguageBuiltins.isInternalCommand(commandText, false);
            isBash4 = LanguageBuiltins.isInternalCommand(commandText, true);
        }

        return bash4 ? isBash4 : isBash3;
    }

    public boolean isInternalCommand() {
        return isInternalCommand(BashProjectSettings.storedSettings(getProject()).isSupportBash4());
    }

    public boolean isExternalCommand() {
        //internal resolve is expensive, so we should cache it
        //we have to listen to psi changes in the file, though
        //otherwise we might still have isExternal set to true even if a
        //a target exists now, e.g. a Bash function with the right name

        return !isInternalCommand() && !isFunctionCall();
    }

    @Override
    public boolean isBashScriptCall() {
        for (PsiReference reference : getReferences()) {
            PsiElement target = reference.resolve();
            if (target instanceof PsiFile) {
                return true;
            }
        }
        return false;
    }

    public boolean isPureAssignment() {
        //pure if neither internal nor generic element is found and if a assignent element is there
        return (commandElement() == null) && hasAssignments();
    }

    public boolean isVarDefCommand() {
        return isInternalCommand()
               && (LanguageBuiltins.varDefCommands.contains(getReferencedCommandName())
                   || LanguageBuiltins.localVarDefCommands.contains(getReferencedCommandName()));
    }

    public boolean hasAssignments() {
        return findChildByType(BashElementTypes.VAR_DEF_ELEMENT) != null;
    }

    @Nullable
    public PsiElement commandElement() {
        ASTNode node = commandElementNode();
        return node != null ? node.getPsi() : null;
    }

    @Nullable
    private ASTNode commandElementNode() {
        return getNode().findChildByType(BashElementTypes.GENERIC_COMMAND_ELEMENT);
    }

    public List<BashPsiElement> parameters() {
        return CachedValuesManager.getCachedValue(this, () -> {
            PsiElement cmd = commandElement();

            List<BashPsiElement> newParameters;
            if (cmd == null) {
                newParameters = Collections.emptyList();
            }
            else {
                newParameters = Lists.newLinkedList();

                PsiElement nextSibling = cmd.getNextSibling();
                while (nextSibling != null) {
                    if (nextSibling instanceof BashPsiElement && !(nextSibling instanceof BashRedirectList)) {
                        newParameters.add((BashPsiElement)nextSibling);
                    }

                    nextSibling = nextSibling.getNextSibling();
                }
            }

            return CachedValueProvider.Result.create(newParameters, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    public BashVarDef[] assignments() {
        return findChildrenByClass(BashVarDef.class);
    }

    @Override
    @Nullable
    public PsiReference getReference() {
        // this is pretty bad, but not fixable without removing isFunctionCall(), etc. and the features (e.g. highlighting) based on that
        for (PsiReference reference : getReferences()) {
            PsiElement target = reference.resolve();
            if (target != null) {
                return reference;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this);
    }

    @Nullable
    public String getReferencedCommandName() {
        return CachedValuesManager.getCachedValue(this, () -> {
            String name;

            T stub = getStub();
            if (stub instanceof BashCommandStub) {
                name = ((BashCommandStub)stub).getBashCommandName();
            }
            else {
                ASTNode command = commandElementNode();
                name = command != null ? command.getText() : null;
            }
            return CachedValueProvider.Result.create(name, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    @Override
    public boolean canNavigate() {
        return isFunctionCall() || isBashScriptCall();
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            BashVisitor v = (BashVisitor)visitor;
            if (isInternalCommand()) {
                v.visitInternalCommand(this);
            }
            else {
                v.visitGenericCommand(this);
            }
        }
        else {
            visitor.visitElement(this);
        }
    }

    @Override
    public ItemPresentation getPresentation() {
        //fixme caching?
        return new ItemPresentation() {
            public String getPresentableText() {
                final PsiElement element = AbstractBashCommand.this.commandElement();
                return element == null ? "unknown" : element.getText();
            }

            public String getLocationString() {
                return null;
            }

            public Icon getIcon(boolean open) {
                return null;
            }
        };
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState
        state, PsiElement lastParent, @NotNull PsiElement place) {
        return PsiScopesUtil.walkChildrenScopes(this, processor, state, lastParent, place);
    }

    public boolean isIncludeCommand() {
        return false;
    }
}
