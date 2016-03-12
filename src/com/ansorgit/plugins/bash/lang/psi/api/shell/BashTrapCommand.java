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

package com.ansorgit.plugins.bash.lang.psi.api.shell;

import com.ansorgit.plugins.bash.lang.psi.api.BashKeyword;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Defines the PSI interface for the trap command.
 */
public interface BashTrapCommand extends PsiElement, BashKeyword {
    /**
     *
     * @return The element representing the signal handler
     */
    @Nullable
    PsiElement getSignalHandlerElement();

    List<PsiElement> getSignalSpec();
}
