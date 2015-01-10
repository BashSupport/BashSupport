/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractBashInspection.java, Class: AbstractBashInspection
 * Last modified: 2013-05-09
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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.editor.inspections.InspectionProvider;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalInspectionTool;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for Bash inspections.
 *
 * @author Joachim Ansorg
 */
abstract class AbstractBashInspection extends LocalInspectionTool {
    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WEAK_WARNING;
    }

    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return InspectionProvider.BASH_FAMILY;
    }
}
