package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.jetbrains.PsiScopesUtil;
import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.FileInclusionManager;
import com.ansorgit.plugins.bash.lang.psi.api.*;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashRedirectList;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashTrapCommand;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.Keys;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashCommandStub;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashCommandStubBase;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashFunctionNameIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIncludeCommandIndex;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashScriptNameIndex;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashSearchScopes;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileSystemUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceUtil;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class AbstractBashCommand<T extends BashCommandStubBase> extends BashBaseStubElementImpl<T> implements BashCommand, Keys {
    private final PsiReference functionReference = new CachedFunctionReference(this);
    private final PsiReference bashFileReference = new BashFileReference(this);

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
    public boolean isLanguageInjectionContainerFor(PsiElement candidate) {
        return false;
       /* String referencedCommandName = getReferencedCommandName();
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

        BashTrapCommand trapCommand = PsiTreeUtil.getStubOrPsiParentOfType(this, BashTrapCommand.class);
        PsiElement signalHandlerElement = trapCommand != null ? trapCommand.getSignalHandlerElement() : null;
        if (signalHandlerElement != null) {
            boolean multipleWords = signalHandlerElement.getText().contains(" ");
            return multipleWords && PsiManager.getInstance(getProject()).areElementsEquivalent(candidate, signalHandlerElement);
        }

        return false;        */
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

    private static class CachedFunctionReference extends CachingReference implements BashReference, BindablePsiReference {
        private final AbstractBashCommand<?> cmd;

        public CachedFunctionReference(AbstractBashCommand<?> cmd) {
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

            Project project = cmd.getProject();
            PsiFile currentFile = cmd.getContainingFile();

            GlobalSearchScope allFiles = FileInclusionManager.includedFilesUnionScope(currentFile);
            Collection<BashFunctionDef> functionDefs = StubIndex.getElements(BashFunctionNameIndex.KEY, referencedName, project, allFiles, BashFunctionDef.class);

            ResolveState initial = ResolveState.initial();
            for (BashFunctionDef functionDef : functionDefs) {
                processor.execute(functionDef, initial);
            }

            //find include commands which are relevant for the start element
            if (!processor.hasResults()) {
                Set<BashFile> includingFiles = FileInclusionManager.findIncluders(project, currentFile);

                List<GlobalSearchScope> scopes = Lists.newLinkedList();
                for (BashFile file : includingFiles) {
                    scopes.add(GlobalSearchScope.fileScope(file));
                }

                if (!scopes.isEmpty()) {
                    GlobalSearchScope scope = GlobalSearchScope.union(scopes.toArray(new GlobalSearchScope[scopes.size()]));

                    functionDefs = StubIndex.getElements(BashFunctionNameIndex.KEY, referencedName, project, scope, BashFunctionDef.class);

                    for (BashFunctionDef def : functionDefs) {
                        processor.execute(def, initial);
                    }
                }
            }

            processor.prepareResults();

            return processor.hasResults() ? processor.getBestResult(true, cmd) : null;


            //PsiFile currentFile = BashPsiUtils.findFileContext(cmd, true);
            /*
            boolean walkOn = BashPsiUtils.treeWalkUp(processor, cmd, currentFile, ResolveState.initial());
            if (!walkOn) {
                return processor.hasResults() ? processor.getBestResult(true, cmd) : null;
            }

            //we need to look into the files which include this command's containingFile.
            //a function call might reference a command from one of the including files
            Set<BashFile> includingFiles = FileInclusionManager.findIncluders(cmd.getProject(), currentFile);
            for (BashFile file : includingFiles) {
                walkOn = BashPsiUtils.treeWalkUp(processor, file.getLastChild(), file, ResolveState.initial());
                if (!walkOn) {
                    return processor.hasResults() ? processor.getBestResult(true, cmd) : null;
                }
            }

            return null;  */
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
        private ElementManipulator<AbstractBashCommand<?>> getManipulator() {
            ElementManipulator<AbstractBashCommand<?>> manipulator = ElementManipulators.<AbstractBashCommand<?>>getManipulator(cmd);
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

    private static class BashFileReference extends CachingReference implements BashReference, BindablePsiReference {
        private final AbstractBashCommand<?> cmd;

        public BashFileReference(AbstractBashCommand<?> cmd) {
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

            //clean it up
            String fileName = PathUtil.getFileName(referencedName);

            //fixme resolve path to full canoncial path and then look it up
            /*GlobalSearchScope scope = BashSearchScopes.moduleScope(cmd.getContainingFile());
            Collection<BashFile> files = StubIndex.getElements(BashScriptNameIndex.KEY, referencedName, cmd.getProject(), scope, BashFile.class);
            if (files.isEmpty()) {
                return null;
            } */
            GlobalSearchScope scope = BashSearchScopes.moduleScope(cmd.getContainingFile());
            PsiFileSystemItem[] files = FilenameIndex.getFilesByName(cmd.getProject(), fileName, scope, false);
            if (files.length == 0) {
                return null;
            }

            PsiFile currentFile = cmd.getContainingFile();
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
        private ElementManipulator<AbstractBashCommand<?>> getManipulator() {
            ElementManipulator<AbstractBashCommand<?>> manipulator = ElementManipulators.<AbstractBashCommand<?>>getManipulator(cmd);
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
                //findRelativeFilePath already leaves the injection host file
                PsiFile currentFile = cmd.getContainingFile();
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
