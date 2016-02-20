package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.jetbrains.PsiScopesUtil;
import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.*;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashRedirectList;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.Keys;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashCommandStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashCommandStubBase;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class AbstractBashCommand<T extends BashCommandStubBase> extends BashBaseStubElementImpl<T> implements BashCommand, Keys {
    private final PsiReference functionReference = new CachedFunctionReference(this);
    private final PsiReference functionReferenceDumbMode = new CachedFunctionReferenceDumbMode(this);

    private final PsiReference bashFileReference = new BashFileReference(this);
    private final PsiReference bashFileReferenceDumbMode = new BashFileReferenceDumbMode(this);

    private String referencedCommandName;
    private boolean hasReferencedCommandName = false;
    private Boolean isInternalCommandBash3;
    private Boolean isInternalCommandBash4;
    private List<BashPsiElement> parameters;

    public AbstractBashCommand(ASTNode astNode, String name) {
        super(astNode, name);
    }

    public AbstractBashCommand(T stub, IStubElementType nodeType, String name) {
        super(stub, nodeType, name);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        this.hasReferencedCommandName = false;
        this.referencedCommandName = null;
        this.isInternalCommandBash3 = null;
        this.isInternalCommandBash4 = null;
        this.parameters = null;
    }

    public boolean isGenericCommand() {
        T stub = getStub();
        if (stub != null) {
            return stub.isGenericCommand();
        }

        return commandElementNode() != null;
    }

    public boolean isFunctionCall() {
        boolean isDumb = DumbService.isDumb(getProject());
        if (isDumb) {
            return isGenericCommand() && functionReferenceDumbMode.resolve() != null;
        }

        return isGenericCommand() && functionReference.resolve() != null;
    }

    @Override
    public boolean isInternalCommand(boolean bash4) {
        T stub = getStub();
        if (stub != null) {
            return stub.isInternalCommand(bash4);
        }

        if (isInternalCommandBash3 == null || isInternalCommandBash4 == null) {
            isInternalCommandBash3 = false;
            isInternalCommandBash4 = false;

            if (isGenericCommand()) {
                String commandText = getReferencedCommandName();
                isInternalCommandBash3 = LanguageBuiltins.isInternalCommand(commandText, false);
                isInternalCommandBash4 = LanguageBuiltins.isInternalCommand(commandText, true);
            }
        }

        return bash4 ? isInternalCommandBash4 : isInternalCommandBash3;
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
        if (DumbService.isDumb(getProject())) {
            return bashFileReferenceDumbMode.resolve() != null;
        }

        return bashFileReference.resolve() != null;
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
        if (parameters == null) {
            PsiElement cmd = commandElement();
            if (cmd == null) {
                parameters = Collections.emptyList();
            } else {
                parameters = Lists.newLinkedList();

                PsiElement nextSibling = cmd.getNextSibling();
                while (nextSibling != null) {
                    if (nextSibling instanceof BashPsiElement && !(nextSibling instanceof BashRedirectList)) {
                        parameters.add((BashPsiElement) nextSibling);
                    }

                    nextSibling = nextSibling.getNextSibling();
                }
            }
        }

        return parameters;
    }

    public BashVarDef[] assignments() {
        return findChildrenByClass(BashVarDef.class);
    }

    @Override
    public PsiReference getReference() {
        if (isFunctionCall()) {
            return functionReference;
        }

        if (isInternalCommand()) {
            //a reference is required for QuickDoc support, camMavigate avoids the "Go to definition" nvaigation
            return BashPsiUtils.selfReference(this);
        }

        if (DumbService.isDumb(getProject())) {
            return bashFileReferenceDumbMode;
        }

        return bashFileReference;
    }

    @Nullable
    public String getReferencedCommandName() {
        T stub = getStub();
        if (stub instanceof BashCommandStub) {
            return ((BashCommandStub) stub).getBashCommandName();
        }

        if (!hasReferencedCommandName) {
            ASTNode command = commandElementNode();
            referencedCommandName = command != null ? command.getText() : null;
            hasReferencedCommandName = true;
        }

        return referencedCommandName;
    }

    @Override
    public boolean canNavigate() {
        return isFunctionCall() || isBashScriptCall();
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
        } else {
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
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return PsiScopesUtil.walkChildrenScopes(this, processor, state, lastParent, place);
    }

    public boolean isIncludeCommand() {
        return false;
    }

}
