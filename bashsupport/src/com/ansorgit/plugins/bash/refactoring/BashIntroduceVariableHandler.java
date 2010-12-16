package com.ansorgit.plugins.bash.refactoring;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * This handler is for the "Introduce variable" refactoring feature of IntelliJ IDEA.
 * <p/>
 * User: jansorg
 * Date: 10.12.10
 * Time: 21:05
 */
class BashIntroduceVariableHandler implements RefactoringActionHandler {
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement elementAtCaret = file.findElementAt(caretOffset);

        if (elementAtCaret != null) {
            introduceVariable(project, elementAtCaret, editor, file, dataContext);
        }
    }

    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        //no op
    }

    private boolean introduceVariable(Project project, @NotNull PsiElement source, Editor editor, PsiFile file, DataContext dataContext) {
        if (source instanceof BashWord || source.getNode().getElementType() == BashTokenTypes.WORD) {
            String text = source.getText();
            if (text == null) {
                return false;
            }

            BashFunctionDef functionScope = BashPsiUtils.findNextVarDefFunctionDefScope(source);

            //find the best suitable place for the new variable
            //PsiNamedElement suitablePlace = PsiTreeUtil.getParentOfType(source, BashFile.class, BashFunctionDef.class);

            //create new variable placeholder
            PsiElement newVar = BashChangeUtil.createVariable(project, "myVar", false);

            //replace source with new element
            PsiElement newElement = BashPsiUtils.replaceElement(source, newVar);


            //editor.getDocument().insertString(0, "myVar=" + text + "\n");
        }

        return false;
    }

}
