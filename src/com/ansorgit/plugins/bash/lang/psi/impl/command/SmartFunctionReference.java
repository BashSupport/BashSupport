package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.FileInclusionManager;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashFunctionNameIndex;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Reference to functions defined in Bash files.
 * This implementation handles dumb mode operations. Index use is turned off during dumb mode operations.
 *
 * @author jansorg
 */
class SmartFunctionReference extends AbstractFunctionReference {

    public SmartFunctionReference(AbstractBashCommand<?> cmd) {
        super(cmd);
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
                GlobalSearchScope scope = GlobalSearchScope.EMPTY_SCOPE;
                for (GlobalSearchScope searchScope : scopes) {
                    scope = scope.uniteWith(searchScope);
                }
                //GlobalSearchScope.union(scopes.toArray(new GlobalSearchScope[scopes.size()]));

                functionDefs = StubIndex.getElements(BashFunctionNameIndex.KEY, referencedName, project, scope, BashFunctionDef.class);

                for (BashFunctionDef def : functionDefs) {
                    processor.execute(def, initial);
                }
            }
        }

        processor.prepareResults();

        return processor.hasResults() ? processor.getBestResult(true, cmd) : null;
    }

}
