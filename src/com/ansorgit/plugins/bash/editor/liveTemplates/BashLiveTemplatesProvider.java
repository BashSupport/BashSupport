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

package com.ansorgit.plugins.bash.editor.liveTemplates;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Live templates for the Bash language.
 */
public class BashLiveTemplatesProvider implements DefaultLiveTemplatesProvider {

    private static final String[] EMPTY = new String[0];

    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return new String[]{"/liveTemplates/Bash"};
    }

    @Nullable
    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return EMPTY;
    }
}
