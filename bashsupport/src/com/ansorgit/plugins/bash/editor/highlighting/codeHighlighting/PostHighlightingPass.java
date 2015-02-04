package com.ansorgit.plugins.bash.editor.highlighting.codeHighlighting;

import com.ansorgit.plugins.bash.editor.inspections.inspections.UnusedFunctionDefInspection;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashFunctionDefName;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.codeInsight.daemon.impl.quickfix.QuickFixAction;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.InspectionProfile;
import com.intellij.codeInspection.reference.UnusedDeclarationFixProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.profile.codeInspection.InspectionProjectProfileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PostHighlightingPass extends TextEditorHighlightingPass {
    private static final Logger LOG = Logger.getInstance("#com.intellij.codeInsight.daemon.impl.PostHighlightingPass");
    @NotNull
    private final Project project;
    @NotNull
    private final PsiFile file;
    @Nullable
    private final Editor editor;
    @NotNull
    private final Document document;
    private HighlightDisplayKey unusedSymbolInspection;
    private int startOffset;
    private int endOffset;
    private Collection<HighlightInfo> highlights;

    PostHighlightingPass(@NotNull Project project, @NotNull PsiFile file, @Nullable Editor editor, @NotNull Document document) {
        super(project, document, true);

        this.project = project;
        this.file = file;
        this.editor = editor;
        this.document = document;

        startOffset = 0;
        endOffset = file.getTextLength();
    }

    @Override
    public List<HighlightInfo> getInfos() {
        return highlights == null ? null : new ArrayList<HighlightInfo>(highlights);
    }

    public static HighlightInfo createUnusedSymbolInfo(@NotNull PsiElement element, @NotNull String message, @NotNull final HighlightInfoType highlightInfoType) {
        HighlightInfo info = HighlightInfo.newHighlightInfo(highlightInfoType).range(element).descriptionAndTooltip(message).create();
        UnusedDeclarationFixProvider[] fixProviders = Extensions.getExtensions(UnusedDeclarationFixProvider.EP_NAME);
        for (UnusedDeclarationFixProvider provider : fixProviders) {
            IntentionAction[] fixes = provider.getQuickFixes(element);
            for (IntentionAction fix : fixes) {
                QuickFixAction.registerQuickFixAction(info, fix);
            }
        }
        return info;
    }

    @Override
    public void doCollectInformation(@NotNull final ProgressIndicator progress) {
        final List<HighlightInfo> highlights = new ArrayList<HighlightInfo>();

        collectHighlights(highlights, progress);
        this.highlights = highlights;
    }

    private void collectHighlights(@NotNull final List<HighlightInfo> result, @NotNull final ProgressIndicator progress) {
        ApplicationManager.getApplication().assertReadAccessAllowed();

        InspectionProfile profile = InspectionProjectProfileManager.getInstance(myProject).getInspectionProfile();

        unusedSymbolInspection = HighlightDisplayKey.findById(UnusedFunctionDefInspection.ID);

        boolean findUnusedFunctions = profile.isToolEnabled(unusedSymbolInspection, file);
        if (findUnusedFunctions) {
            final BashVisitor bashVisitor = new BashVisitor() {
                @Override
                public void visitFunctionDef(BashFunctionDef functionDef) {
                    HighlightingKeys.IS_UNUSED.set(functionDef, null);

                    if (!PsiUtilCore.hasErrorElementChild(functionDef)) {
                        HighlightInfo highlightInfo = processFunctionDef(functionDef, progress);
                        if (highlightInfo != null) {
                            result.add(highlightInfo);
                        }
                    }
                }
            };

            file.accept(new PsiRecursiveElementWalkingVisitor() {
                @Override
                public void visitElement(PsiElement element) {
                    element.accept(bashVisitor);
                    super.visitElement(element);
                }
            });
        }
    }

    private static HighlightInfo processFunctionDef(BashFunctionDef functionDef, ProgressIndicator progress) {
        BashFunctionDefName nameSymbol = functionDef.getNameSymbol();
        if (nameSymbol != null) {
            Query<PsiReference> search = ReferencesSearch.search(functionDef, functionDef.getUseScope(), true);
            progress.checkCanceled();

            PsiReference first = search.findFirst();
            progress.checkCanceled();

            if (first == null) {
                HighlightingKeys.IS_UNUSED.set(functionDef, Boolean.TRUE);

                return createUnusedSymbolInfo(nameSymbol, "Unused function definition", HighlightInfoType.UNUSED_SYMBOL);
            }
        }

        return null;
    }

    @Override
    public void doApplyInformationToEditor() {
        if (highlights == null || highlights.isEmpty()) {
            return;
        }

        UpdateHighlightersUtil.setHighlightersToEditor(myProject, myDocument, startOffset, endOffset, highlights, getColorsScheme(), Pass.POST_UPDATE_ALL);
        BashPostHighlightingPassFactory.markFileUpToDate(file);
    }
}
