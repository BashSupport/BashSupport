/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarDef.java, Class: BashVarDef
 * Last modified: 2013-04-30
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

package com.ansorgit.plugins.bash.lang.psi.api.vars;

import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.api.DocumentationAwareElement;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 14.04.2009
 * Time: 17:01:51
 *
 * @author Joachim Ansorg
 */
public interface BashVarDef extends BashPsiElement, PsiNamedElement, PsiNameIdentifierOwner, NavigationItem, DocumentationAwareElement {
    String getName();

    /**
     * Returns true if this variable definition defines an array variable.
     *
     * @return True if an array variable is being defined
     */
    boolean isArray();

    /**
     * @return True if this definition defines a ready-only variable
     */
    boolean isReadonly();

    /**
     * Returns true if this variable definition if only for the following statement.
     * E.g. "LD_LIBRARY_PATH=/usr oo-writer".
     * A local command doesn't change the current environment.
     *
     * @return True if this command is only local.
     */
    boolean isCommandLocal();

    /**
     * The psi element which is the identifier of the assignment.
     *
     * @return The name element
     */
    @NotNull
    PsiElement findAssignmentWord();

    /**
     * Returns whether this variable definition is a local variable in a function.
     * If a variable is declared using the "local" keyword in a function it is a function local variable,
     * i.e. this method returns true in that case.
     *
     * @return True if it's local in a function
     */
    boolean isFunctionScopeLocal();

    PsiElement findFunctionScope();

    /**
     * @return True if this is a variable definition with assignment, e.g. "export a=1"
     */
    boolean hasAssignmentValue();

    @NotNull
    BashReference getReference();

    /**
     *
     * @return True if the value of the assignment word is static, false otherwise. Something like export "$a"=b is not a static assignment word
     */
    boolean isStaticAssignmentWord();
}
