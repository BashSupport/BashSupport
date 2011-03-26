/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVar.java, Class: BashVar
 * Last modified: 2010-01-21
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

/**
 * Date: 14.04.2009
 * Time: 17:16:16
 *
 * @author Joachim Ansorg
 */
public interface BashVar extends BashPsiElement, BashReference {
    /**
     * Return whether this variable refers to a bash built-in variable or not.
     *
     * @return True if this variable refers to a bash builtin variable
     */
    boolean isBuiltinVar();

    /**
     * Return whether this variable is a composed variable. A composed
     * variable is inside of curly brackets, e.g. ${A}
     *
     * @return True if this variable refers to a bash composed variable
     */
    boolean isParameterExpansion();

    /**
     * @return Returns true if this variable is a reference to a built-in parameter, e.g. $1 or $2 . This method
     *         does return true for $0.
     */
    boolean isParameterReference();

    /**
     * Returns whether this variable has an attached array element reference.
     *
     * @return True if it is an array variable reference.
     */
    boolean isArrayUse();
}
