/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Created by IntelliJ IDEA.
 * User: igork
 * Date: Nov 25, 2002
 * Time: 2:05:49 PM
 * To change this template use Options | File Templates.
 */
package com.ansorgit.plugins.bash.jetbrains;


import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

public class PsiScopesUtil {
    //Taken from the Jetrbains code, it's not included in WebStorm 7 EAP , for example
    public static boolean walkChildrenScopes(@NotNull PsiElement thisElement,
                                             @NotNull PsiScopeProcessor processor,
                                             @NotNull ResolveState state,
                                             PsiElement lastParent,
                                             PsiElement place) {
        PsiElement child = null;
        if (lastParent != null && lastParent.getParent() == thisElement) {
            child = lastParent.getPrevSibling();
            if (child == null) return true; // first element
        }

        if (child == null) {
            child = thisElement.getLastChild();
        }

        while (child != null) {
            if (!child.processDeclarations(processor, state, null, place)) return false;
            child = child.getPrevSibling();
        }

        return true;
    }
}