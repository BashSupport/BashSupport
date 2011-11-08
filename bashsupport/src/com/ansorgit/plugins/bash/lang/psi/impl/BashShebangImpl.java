/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashShebangImpl.java, Class: BashShebangImpl
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 16.04.2009
 * Time: 14:52:33
 *
 * @author Joachim Ansorg
 */
public class BashShebangImpl extends BashPsiElementImpl implements BashShebang {
    private final static Logger log = Logger.getInstance("#bash.BashShebang");

    public BashShebangImpl(final ASTNode astNode) {
        super(astNode, "bash shebang");
        log.debug("Created BashShebangImpl");
    }

    public String shellCommand() {
        String allText = getText();
        if (StringUtils.isEmpty(allText)) {
            return null;
        }

        //shebang line without the prefix #!
        int commandOffset = getShellCommandOffset();
        String line = allText.substring(commandOffset);
        log.debug("shellCommand: " + line);

        String commandLine = hasNewline() ? line.substring(0, line.length() - 1) : line;

        //find the command only, i.e. do not include any params
        int cmdEndIndex = commandLine.indexOf(' ', 0);

        if (cmdEndIndex > 0) {
            commandLine = commandLine.substring(0, cmdEndIndex);
        }

        return commandLine.trim();
    }

    public int getShellCommandOffset() {
        String line = getText();
        if (!line.startsWith("#!")) {
            return 0;
        }

        int offset = 2;
        for (int i = 2; i < line.length() && line.charAt(i) == ' '; i++) {
            offset++;
        }

        return offset;
    }

    @NotNull
    public TextRange commandRange() {
        return TextRange.from(getShellCommandOffset(), shellCommand().length());
    }

    public void updateCommand(String command) {
        log.debug("Updating command to " + command);

        Document document = getContainingFile().getViewProvider().getDocument();
        if (document != null) {
            TextRange textRange = commandRange();
            document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), command);
        } else {
            //fallback
            PsiElement newElement = BashChangeUtil.createShebang(getProject(), command, hasNewline());
            getNode().replaceChild(getNode().getFirstChildNode(), newElement.getNode());
        }
    }

    private boolean hasNewline() {
        return getText().endsWith("\n");
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitShebang(this);
        } else {
            visitor.visitElement(this);
        }
    }


}
