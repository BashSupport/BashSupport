/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.psi.api.function;

import com.ansorgit.plugins.bash.lang.psi.api.BashBlock;
import com.ansorgit.plugins.bash.lang.psi.api.BashFunctionDefName;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.DocumentationAwareElement;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDefContainer;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jansorg
 */
public interface BashFunctionDef extends BashPsiElement, PsiNamedElement, NavigationItem, PsiNameIdentifierOwner, BashVarDefContainer, DocumentationAwareElement {
    /**
     * Returns the function body. A valid function definition always has a valid body.
     *
     * @return The body of the function.
     */
    @Nullable
    BashBlock functionBody();

    @Nullable
    BashFunctionDefName getNameSymbol();

    /**
     * The list of parameters used inside this function definition. Sub-functions are not searched for parameters
     * uses.
     *
     * @return The list of parameter variable uses
     */
    @NotNull
    List<BashPsiElement> findReferencedParameters();

    /**
     * @return Returns all variable definitions which are of local scope, i.e. which are declared as local in this function definition. Variable declarations inside of nested functions are not contained in the result.
     */
    @NotNull
    Set<String> findLocalScopeVariables();
}
