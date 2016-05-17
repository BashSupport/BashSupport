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

package com.ansorgit.plugins.bash.documentation;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.psi.PsiElement;

/**
 * Looks up the documentation for external commands by calling the "man" program.
 * At the moment it uses pre-made HTML files. The aim is that it directly accesses the system's
 * man pages, parses them and returns HTML for it.
 * <br>
 * @author jansorg
 */
class ManpageDocSource extends ClasspathDocSource {
    ManpageDocSource() {
        super("/documentation/external");
    }

    @Override
    String resourceNameForElement(PsiElement element) {
        return commandElement(element).getReferencedCommandName();
    }

    boolean isValid(PsiElement element, PsiElement originalElement) {
        BashCommand command = commandElement(element);

        return command != null && command.isExternalCommand();
    }

    public String documentationUrl(PsiElement element, PsiElement originalElement) {
        if (!isValid(element, originalElement)) {
            return null;
        }

        return String.format("http://man.he.net/man1/%s", commandElement(element).getReferencedCommandName());
    }

    private BashCommand commandElement(PsiElement element) {
        if (element instanceof BashGenericCommand) {
            return BashPsiUtils.findParent(element, BashCommand.class);
        }

        if (element instanceof BashCommand) {
            return (BashCommand) element;
        }

        return null;
    }
}
