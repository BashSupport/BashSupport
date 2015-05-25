/**
 * ****************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashHereDocStartMarkerImpl.java, Class: BashHereDocStartMarkerImpl
 * Last modified: 2010-02-06 10:50
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */

package com.ansorgit.plugins.bash.lang.psi.impl.heredoc;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocStartMarker;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class BashHereDocStartMarkerImpl extends AbstractHeredocMarker implements BashHereDocStartMarker {
    public BashHereDocStartMarkerImpl(final ASTNode astNode) {
        super(astNode, "Bash heredoc start marker", BashHereDocEndMarker.class, true);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitHereDocStartMarker(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public boolean isEvaluatingVariables() {
        PsiElement previous = getPrevSibling();

        String content = previous.getText();
        return content != null && !content.startsWith("\"") && !content.startsWith("'");

    }

    @Override
    public String getMarkerText() {
        String text = getText().trim();

        int start = 0;
        int end = text.length();

        if (text.startsWith("$")) {
            start++;
        }

        if (text.charAt(start) == '"' || text.charAt(start) == '\'') {
            start++;
            end--;
        }

        return text.substring(start, end);
    }
}
