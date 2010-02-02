/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPsiTreeUtils.java, Class: BashPsiTreeUtils
 * Last modified: 2010-01-30
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

package com.ansorgit.plugins.bash.lang.psi.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper methods to work with the psi tree.
 * <p/>
 * User: jansorg
 * Date: Jan 30, 2010
 * Time: 1:36:42 PM
 */
public class BashPsiTreeUtils {
    public static boolean treeWalkDown(@NotNull final PsiScopeProcessor processor, @NotNull final PsiElement entrance,
                                       @Nullable final PsiElement maxScope,
                                       @NotNull final ResolveState state) {
        //first walk down all childs of the entrance
        if (!entrance.processDeclarations(processor, state, entrance, entrance)) return false;

        PsiElement prevParent = entrance;
        PsiElement scope = entrance.getNextSibling();
        if (scope == null) {
            scope = BashPsiUtils.elementAfter(entrance);
        }

        //if not yet found, walkd down all elements after the current psi elemen in the tree
        while (scope != null) {
            if (!scope.processDeclarations(processor, state, prevParent, entrance)) return false;

            if (scope == maxScope) break;
            prevParent = scope;
            scope = prevParent.getNextSibling();
            if (scope == null) {
                scope = BashPsiUtils.elementAfter(prevParent);
            }
        }

        return true;
    }

}
