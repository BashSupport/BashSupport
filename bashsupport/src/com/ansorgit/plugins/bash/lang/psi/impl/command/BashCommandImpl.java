/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashCommandImpl.java, Class: BashCommandImpl
 * Last modified: 2010-07-08
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashRedirectList;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.Keys;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Date: 12.04.2009
 * Time: 21:34:37
 *
 * @author Joachim Ansorg
 */
public class BashCommandImpl extends BashPsiElementImpl implements BashCommand, Keys {
    private boolean isInternal;
    private boolean isExternal;

    public BashCommandImpl(ASTNode astNode) {
        this(astNode, "Bash command");
    }

    public BashCommandImpl(ASTNode astNode, String name) {
        super(astNode, name);

        updateCache();
    }

    private void updateCache() {
        isInternal = findChildByType(BashElementTypes.INTERNAL_COMMAND_ELEMENT) != null;
        isExternal = findChildByType(BashElementTypes.GENERIC_COMMAND_ELEMENT) != null;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        updateCache();
    }

    public boolean isFunctionCall() {
        PsiElement commandElement = commandElement();
        if (commandElement == null || commandElement.getNode() == null) {
            return false;
        }

        return commandElement.getNode().getElementType() == BashElementTypes.GENERIC_COMMAND_ELEMENT && internalResolve() != null;
    }

    public boolean isInternalCommand() {
        return isInternal;
    }

    public boolean isExternalCommand() {
        //internal resolve is expensive, so we should cache it
        //we have to listen to psi changes in the file, though
        //otherwise we might still have isExternal set to true even if a
        //a target exists now, e.g. a bash function witht the right name
        return isExternal && (internalResolve() == null);
    }

    public boolean isPureAssignment() {
        //pure if neither internal nor generic element is found and if a assignent element is there
        return (commandElement() == null) && hasAssignments();
    }

    public boolean isVarDefCommand() {
        return isInternalCommand() && (LanguageBuiltins.varDefCommands.contains(getReferencedName())
                || LanguageBuiltins.localVarDefCommands.contains(getReferencedName()));
    }

    public boolean hasAssignments() {
        return assignments().size() > 0;
    }

    public PsiElement commandElement() {
        PsiElement element = findChildByType(BashElementTypes.INTERNAL_COMMAND_ELEMENT);
        if (element != null) {
            return element;
        }

        return findChildByType(BashElementTypes.GENERIC_COMMAND_ELEMENT);
    }

    public List<BashPsiElement> parameters() {
        PsiElement cmd = commandElement();
        if (cmd == null) {
            return Collections.emptyList();
        }

        List<BashPsiElement> result = Lists.newLinkedList();

        PsiElement nextSibling = cmd.getNextSibling();
        while (nextSibling != null) {
            if (nextSibling instanceof BashPsiElement && !(nextSibling instanceof BashRedirectList)) {
                result.add((BashPsiElement) nextSibling);
            }

            nextSibling = nextSibling.getNextSibling();
        }

        return result;
    }

    public List<BashVarDef> assignments() {
        return Arrays.asList(findChildrenByClass(BashVarDef.class));
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    public String getReferencedName() {
        final PsiElement element = commandElement();
        if (element != null) {
            return element.getText();
        }

        return null;
    }

    public PsiElement getElement() {
        return this;
    }

    public TextRange getRangeInElement() {
        final PsiElement element = commandElement();
        if (element == null) {
            return TextRange.from(0, getTextLength());
        }

        return TextRange.from(element.getStartOffsetInParent(), element.getTextLength());
    }

    private PsiElement internalResolve() {
        final String referencedName = getReferencedName();
        if (referencedName == null) {
            return null;
        }

        final BashFunctionProcessor processor = new BashFunctionProcessor(referencedName);

        boolean walkOn = PsiTreeUtil.treeWalkUp(processor, this, getContainingFile(), ResolveState.initial());
        if (!walkOn) {
            return processor.hasResults() ? processor.getBestResult(true, this) : null;
        }

        return null;
    }

    public PsiElement resolve() {
        if (isInternalCommand()) {
            return this;
        }

        PsiElement result = internalResolve();
        if (isExternal && result == null) {
            return null;
        }

        return result;
        //fixme for doc provider we should return null for internal commands
        //fixme or better: add own implemenation for internal commands
    }

    @NotNull
    public String getCanonicalText() {
        return getReferencedName();
    }

    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        if (StringUtil.isEmpty(newName)) {
            return null;
        }

        final PsiElement original = commandElement();
        final PsiElement replacement = BashChangeUtil.createWord(getProject(), newName);

        getNode().replaceChild(original.getNode(), replacement.getNode());
        return this;
    }

    public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        throw new IncorrectOperationException("bindToElement not implemented");
    }

    public boolean isReferenceTo(PsiElement element) {
        if (element == this) {
            return true;
        }

        return element instanceof PsiNamedElement && Comparing.equal(getReferencedName(), ((PsiNamedElement) element).getName()) && resolve() == element;
    }

    @org.jetbrains.annotations.NotNull
    public Object[] getVariants() {
        List<Object> variants = Lists.newArrayList();

        BashFunctionVariantsProcessor processor = new BashFunctionVariantsProcessor();
        PsiTreeUtil.treeWalkUp(processor, this, this.getContainingFile(), ResolveState.initial());

        variants.addAll(processor.getFunctionDefs());

        if (BashProjectSettings.storedSettings(getProject()).isAutocompleteBuiltinCommands()) {
            variants.addAll(LanguageBuiltins.commands);
        }

        if (BashProjectSettings.storedSettings(getProject()).isSupportBash4()) {
            variants.addAll(LanguageBuiltins.commands_v4);
        }

        return variants.toArray();
    }

    public boolean isSoft() {
        return false;
    }

    @Override
    public boolean canNavigate() {
        return isFunctionCall();
    }

    @Override
    public boolean canNavigateToSource() {
        return isFunctionCall();
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            BashVisitor v = (BashVisitor) visitor;
            if (isInternalCommand()) {
                v.visitInternalCommand(this);
            } else {
                v.visitGenericCommand(this);
            }

            if (isIncludeCommand()) {
                v.visitIncludeCommand(this);
            }
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {
                final PsiElement element = BashCommandImpl.this.commandElement();
                return element == null ? "unknown" : element.getText();
            }

            public String getLocationString() {
                return null;
            }

            public Icon getIcon(boolean open) {
                return null;
            }

            public TextAttributesKey getTextAttributesKey() {
                return null;
            }
        };
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        boolean result = super.processDeclarations(processor, state, lastParent, place);

        if (isIncludeCommand()) {
            PsiFile containingFile = getContainingFile();
            PsiFile includedFile = BashPsiUtils.findIncludedFile(this);

            Multimap<VirtualFile, PsiElement> visitedFiles = state.get(visitedIncludeFiles);
            visitedFiles.put(containingFile.getVirtualFile(), null);

            if (includedFile != null && !visitedFiles.containsKey(includedFile.getVirtualFile())) {
                //mark the file as visited before the actual visit, otherwise we'll get a stack overflow
                visitedFiles.put(includedFile.getVirtualFile(), this);
                state = state.put(visitedIncludeFiles, visitedFiles);

                return includedFile.processDeclarations(processor, state, lastParent, place);
            }
        }

        return result;
    }

    public boolean isIncludeCommand() {
        return ".".equals(getReferencedName()) || "source".equals(getReferencedName());
    }
}
