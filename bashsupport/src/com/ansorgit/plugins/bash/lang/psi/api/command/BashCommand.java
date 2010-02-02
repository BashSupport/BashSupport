/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashCommand.java, Class: BashCommand
 * Last modified: 2009-12-04
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

package com.ansorgit.plugins.bash.lang.psi.api.command;

import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * A bash command psi element represents a simple command of the grammar. This can
 * either be an internal bash command, an external bash command or a function call.
 * It's also possible that a command has just assignments and no actual call to
 * a function or internal/external command. Then it's a pure assignment call.
 * <p/>
 * <p/>
 * Date: 12.04.2009
 * Time: 21:33:53
 *
 * @author Joachim Ansorg
 */
public interface BashCommand extends BashPsiElement, BashReference {
    /**
     * Checks whether this command is a call to a declared function.
     *
     * @return True if this command is a function call.
     */
    public boolean isFunctionCall();

    /**
     * Checks whether this command is a call of an bash-internal command, like "echo" or "cd".
     *
     * @return True if this commmand is an internal command.
     */
    public boolean isInternalCommand();

    /**
     * Returns true if this command is a call to an external program.
     *
     * @return True if this is an external command.
     */
    public boolean isExternalCommand();

    /**
     * Returns true if this command is a call to an external program.
     *
     * @return True if this only contains assignments without an actual command.
     */
    public boolean isPureAssignment();

    /**
     * Returns true if this command is a variable declaring command (e.g. export or read)
     *
     * @return True if it declares variables visible on the outside
     */
    public boolean isVarDefCommand();

    /**
     * Returns whether an assignment expression is part of this simple command statement.
     *
     * @return True if one ore more assignments are done for the command.
     */
    public boolean hasAssignments();

    /**
     * Returns the element which represents the executed command.
     * i.e. "echo" of the statement "a=b echo a b c"
     *
     * @return The element
     */
    public PsiElement commandElement();

    /**
     * Returns the elements which are parameters to the command
     */
    public List<BashPsiElement> parameters();

    /**
     * Returns the assignments which are available in this command
     */
    public List<BashVarDef> assignments();

    public String getReferencedName();
}
