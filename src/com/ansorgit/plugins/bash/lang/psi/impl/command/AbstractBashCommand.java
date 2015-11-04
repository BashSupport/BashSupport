package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.jetbrains.PsiScopesUtil;
import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.FileInclusionManager;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashRedirectList;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashTrapCommand;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.Keys;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AbstractBashCommand<T extends StubElement> extends BashBaseStubElementImpl<T> implements BashCommand, Keys {
    private final static Key<CachedValue<Boolean>> KEY_INTERNAL = Key.create("internal");
    private final static Key<CachedValue<Boolean>> KEY_EXTERNAL_OR_FUNCTION = Key.create("external");
    private final static Key<CachedValue<Boolean>> KEY_FUNCTION_CALL = Key.create("functionCall");
    private final PsiReference functionReference = new CachedFunctionReference<T>(this);
    private final PsiReference bashFileReference = new BashFileReference<T>(this);

    public AbstractBashCommand(ASTNode astNode, String name) {
        super(astNode, name);
    }

    public AbstractBashCommand(T stub, IStubElementType nodeType, String name) {
        super(stub, nodeType, name);
    }

    public boolean isFunctionCall() {
        if (DumbService.isDumb(getProject())) {
            return false;
        }

        CachedValuesManager manager = CachedValuesManager.getManager(getProject());
        return manager.getCachedValue(this, KEY_FUNCTION_CALL, new CachedValueProvider<Boolean>() {
            @Override
            public Result<Boolean> compute() {
                boolean isFunctionCall;

                PsiElement commandElement = commandElement();
                if (commandElement == null) {
                    isFunctionCall = false;
                } else {
                    ASTNode node = commandElement.getNode();
                    isFunctionCall = node != null && node.getElementType() == BashElementTypes.GENERIC_COMMAND_ELEMENT && functionReference.resolve() != null;
                }


                return Result.create(isFunctionCall, AbstractBashCommand.this);
            }
        }, false);
    }

    public boolean isInternalCommand() {
        CachedValuesManager manager = CachedValuesManager.getManager(getProject());
        return manager.getCachedValue(this, KEY_INTERNAL, new CachedValueProvider<Boolean>() {
            @Nullable
            @Override
            public Result<Boolean> compute() {
                ASTNode command = getNode().findChildByType(BashElementTypes.GENERIC_COMMAND_ELEMENT);
                boolean result = command != null && LanguageBuiltins.isInternalCommand(command.getText(), BashProjectSettings.storedSettings(getProject()).isSupportBash4());

                return Result.create(result, AbstractBashCommand.this);
            }
        }, false);
    }

    public boolean isExternalCommand() {
        //internal resolve is expensive, so we should cache it
        //we have to listen to psi changes in the file, though
        //otherwise we might still have isExternal set to true even if a
        //a target exists now, e.g. a Bash function with the right name

        CachedValuesManager manager = CachedValuesManager.getManager(getProject());
        Boolean cachedValue = manager.getCachedValue(this, KEY_EXTERNAL_OR_FUNCTION, new CachedValueProvider<Boolean>() {
            @Nullable
            @Override
            public Result<Boolean> compute() {
                ASTNode command = getNode().findChildByType(BashElementTypes.GENERIC_COMMAND_ELEMENT);
                boolean result = command != null && !LanguageBuiltins.isInternalCommand(command.getText(), BashProjectSettings.storedSettings(getProject()).isSupportBash4());

                return Result.create(result, AbstractBashCommand.this);
            }
        }, false);

        return cachedValue && !isFunctionCall();
    }

    @Override
    public boolean isBashScriptCall() {
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

    public PsiElement commandElement() {
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

    public BashVarDef[] assignments() {
        return findChildrenByClass(BashVarDef.class);
    }

    @Override
    public PsiReference getReference() {
        if (isFunctionCall()) {
            return functionReference;
        }

        if (isInternalCommand()) {
            //return new SelfReference(this);
            return null;
        }

        return bashFileReference;
    }

    @Nullable
    public String getReferencedCommandName() {
        final PsiElement element = commandElement();
        if (element != null) {
            return element.getText();
        }

        return null;
    }

    @Override
    public boolean isLanguageInjectionContainerFor(PsiElement candidate) {
        String referencedCommandName = getReferencedCommandName();
        if (referencedCommandName == null) {
            return false;
        }

        if ("eval".equals(referencedCommandName)) {
            //only the first child is evaluated as a bash document, the remaining parameters are the options passed to the new bash document
            //e.g.  eval "echo $@" "first" "second"  outputs "first second"
            PsiElement command = commandElement();
            if (command == null) {
                return false;
            }

            PsiElement injectionElement = BashPsiUtils.findNextSibling(command, BashTokenTypes.WHITESPACE);
            return injectionElement != null && PsiManager.getInstance(getProject()).areElementsEquivalent(injectionElement, candidate);
        }

        BashTrapCommand trapCommand = PsiTreeUtil.getParentOfType(this, BashTrapCommand.class);
        PsiElement signalHandlerElement = trapCommand != null ? trapCommand.getSignalHandlerElement() : null;
        if (signalHandlerElement != null) {
            boolean multipleWords = signalHandlerElement.getText().contains(" ");
            return multipleWords && PsiManager.getInstance(getProject()).areElementsEquivalent(candidate, signalHandlerElement);
        }

        return false;
    }

    @Override
    public boolean canNavigate() {
        return isFunctionCall() || isBashFileCall();
    }

    /**
     * @return True if this command references a project Bash file.
     */
    private boolean isBashFileCall() {
        return false;
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

    private static class CachedFunctionReference<T extends StubElement> extends CachingReference implements BashReference, BindablePsiReference {
        private final AbstractBashCommand<T> cmd;

        public CachedFunctionReference(AbstractBashCommand<T> cmd) {
            this.cmd = cmd;
        }

        @Override
        public String getReferencedName() {
            return cmd.getReferencedCommandName();
        }

        @Nullable
        @Override
        public PsiElement resolveInner() {
            final String referencedName = cmd.getReferencedCommandName();
            if (referencedName == null) {
                return null;
            }

            final ResolveProcessor processor = new BashFunctionProcessor(referencedName);

            PsiFile currentFile = BashPsiUtils.findFileContext(cmd);

            boolean walkOn = PsiTreeUtil.treeWalkUp(processor, cmd, currentFile, ResolveState.initial());
            if (!walkOn) {
                return processor.hasResults() ? processor.getBestResult(true, cmd) : null;
            }

            //we need to look into the files which include this command's containingFile.
            //a function call might reference a command from one of the including files
            Set<BashFile> includingFiles = FileInclusionManager.findIncluders(cmd.getProject(), currentFile);
            for (BashFile file : includingFiles) {
                walkOn = PsiTreeUtil.treeWalkUp(processor, file.getLastChild(), file, ResolveState.initial());
                if (!walkOn) {
                    return processor.hasResults() ? processor.getBestResult(true, cmd) : null;
                }
            }

            return null;
        }

        @Override
        public PsiElement getElement() {
            return cmd;
        }

        @Override
        public TextRange getRangeInElement() {
            return getManipulator().getRangeInElement(cmd);
        }

        @NotNull
        private ElementManipulator<AbstractBashCommand<T>> getManipulator() {
            ElementManipulator<AbstractBashCommand<T>> manipulator = ElementManipulators.getManipulator(cmd);
            if (manipulator == null) {
                throw new IncorrectOperationException("No element manipulator found for " + cmd);
            }
            return manipulator;
        }

        @NotNull
        @Override
        public String getCanonicalText() {
            String referencedName = cmd.getReferencedCommandName();
            return referencedName != null ? referencedName : "";
        }

        @Override
        public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
            return getManipulator().handleContentChange(cmd, newElementName);
        }

        @Override
        public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
            if (element instanceof BashFunctionDef) {
                return handleElementRename(((BashFunctionDef) element).getName());
            }

            throw new IncorrectOperationException("unsupported for element " + element);
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return EMPTY_ARRAY;
        }
    }

    private static class BashFileReference<T extends StubElement> extends CachingReference implements BashReference, BindablePsiReference {
        private final AbstractBashCommand<T> cmd;

        public BashFileReference(AbstractBashCommand<T> cmd) {
            this.cmd = cmd;
        }

        @Override
        public String getReferencedName() {
            return cmd.getReferencedCommandName();
        }

        @Nullable
        @Override
        public PsiElement resolveInner() {
            final String referencedName = cmd.getReferencedCommandName();
            if (referencedName == null) {
                return null;
            }

            PsiFile currentFile = BashPsiUtils.findFileContext(cmd);
            return BashPsiFileUtils.findRelativeFile(currentFile, referencedName);
        }

        @Override
        public PsiElement getElement() {
            return cmd;
        }

        @Override
        public TextRange getRangeInElement() {
            return getManipulator().getRangeInElement(cmd);
        }

        @NotNull
        private ElementManipulator<AbstractBashCommand<T>> getManipulator() {
            ElementManipulator<AbstractBashCommand<T>> manipulator = ElementManipulators.getManipulator(cmd);
            if (manipulator == null) {
                throw new IncorrectOperationException("No element manipulator found for " + cmd);
            }
            return manipulator;
        }

        @NotNull
        @Override
        public String getCanonicalText() {
            String referencedName = cmd.getReferencedCommandName();
            return referencedName != null ? referencedName : "";
        }

        @Override
        public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
            return getManipulator().handleContentChange(cmd, newElementName);
        }

        @Override
        public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
            if (element instanceof PsiFile) {
                PsiFile currentFile = BashPsiUtils.findFileContext(cmd);

                String relativeFilePath = BashPsiFileUtils.findRelativeFilePath(currentFile, (PsiFile) element);

                return handleElementRename(relativeFilePath);
            }

            throw new IncorrectOperationException("unsupported for element " + element);
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return EMPTY_ARRAY;
        }
    }
}
