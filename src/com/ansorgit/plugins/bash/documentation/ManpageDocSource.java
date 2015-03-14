/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ManpageDocSource.java, Class: ManpageDocSource
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

package com.ansorgit.plugins.bash.documentation;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.intellij.psi.PsiElement;

/**
 * Looks up the documentation for external commands by calling the "man" program.
 * At the moment it uses pre-made HTML files. The aim is that it directly accesses the system's
 * man pages, parses them and returns HTML for it.
 * <p/>
 * Date: 03.05.2009
 * Time: 20:22:55
 *
 * @author Joachim Ansorg
 */
class ManpageDocSource extends ClasspathDocSource {
    protected ManpageDocSource() {
        super("/documentation/external");
    }

    boolean isValid(PsiElement element, PsiElement originalElement) {
        return element instanceof BashCommand && ((BashCommand) element).isExternalCommand();
    }

    public String documentationUrl(PsiElement element, PsiElement originalElement) {
        if (!isValid(element, originalElement)) return null;

        BashCommand cmd = (BashCommand) element;
        return "http://www.linuxmanpages.com/man1/" + cmd.getReferencedName() + ".1.php";
    }
}
