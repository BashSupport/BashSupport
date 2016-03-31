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

package com.ansorgit.plugins.bash.lang.psi.api;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author jansorg
 */
public interface BashShebang extends BashPsiElement {
    /**
     * @param withParams True if the interpreter options should be included in the command
     * @return The shell command given in the shebang line, e.g. /bin/sh
     */
    String shellCommand(boolean withParams);

    /**
     *
     * @return The parameters of the shell command, if available.
     */
    String shellCommandParams();

    @NotNull
    TextRange commandAndParamsRange();

    void updateCommand(String command, @Nullable TextRange replacementRange);

    @NotNull
    TextRange commandRange();

    int getShellCommandOffset();
}
