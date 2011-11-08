/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashIntroduceVariableHandler.java, Class: BashIntroduceVariableHandler
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.refactoring;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * This handler is for the "Introduce variable" refactoring feature of IntelliJ IDEA.
 * <p/>
 * It is able to extract partial values of word and string tokens, i.e. substrings.
 * <p/>
 * The new variable is either placed inside of the current function (if available) or into the global scope
 * of the current Bash script.
 * <p/>
 * Also, it is able to search and replace duplicate values.
 * <p/>
 * <p/>
 * User: jansorg
 * Date: 10.12.10
 * Time: 21:05
 */
class BashIntroduceVariableHandler implements RefactoringActionHandler {
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        SelectionModel selectionModel = editor.getSelectionModel();
        if (selectionModel.hasSelection()) {
            int start = selectionModel.getSelectionStart();
            int end = selectionModel.getSelectionEnd();

            ArrayList<PsiElement> elements = new ArrayList<PsiElement>();

            PsiElement element = file.findElementAt(start);
            while (element != null) {
                elements.add(element);

                PsiElement next = element.getNextSibling();
                if (next != null && next.getTextRange().getStartOffset() < end && next.getTextRange().getEndOffset() <= end) {
                    element = next;
                }
            }

            if (!elements.isEmpty()) {
                invoke(project, elements.toArray(new PsiElement[elements.size()]), dataContext);
            }
        } else {
            PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
            invoke(project, new PsiElement[]{element}, dataContext);
        }

    }

    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {

    }
}
