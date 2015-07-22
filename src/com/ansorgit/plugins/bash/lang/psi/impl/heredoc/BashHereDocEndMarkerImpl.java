/**
 * ****************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashHereDocEndMarkerImpl.java, Class: BashHereDocEndMarkerImpl
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
import com.ansorgit.plugins.bash.lang.psi.api.command.BashComposedCommand;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocStartMarker;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Heredoc end marker implementation.
 *
 * @author jansorg
 */
public class BashHereDocEndMarkerImpl extends AbstractHeredocMarker implements BashHereDocEndMarker {
    public BashHereDocEndMarkerImpl(final ASTNode astNode) {
        super(astNode, "Bash heredoc end marker");
    }

    @Nullable
    @Override
    protected PsiElement resolveInner() {
        final String markerName = getMarkerText();
        if (markerName == null || markerName.isEmpty()) {
            return null;
        }

        //walk to the command containing this heredoc end marker
        //fixme limitation with multiple heredocs using the same marker name more than once

        BashComposedCommand parent = BashPsiUtils.findParent(this, BashComposedCommand.class);
        if (parent == null) {
            return null;
        }

        final List<BashHereDocStartMarker> startMarkers = Lists.newLinkedList();

        BashPsiUtils.visitRecursively(parent, new BashVisitor() {
            @Override
            public void visitHereDocStartMarker(BashHereDocStartMarker marker) {
                if (markerName.equals(marker.getMarkerText())) {
                    startMarkers.add(marker);
                }
            }
        });

        return startMarkers.isEmpty() ? null : startMarkers.get(0);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitHereDocEndMarker(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public String getMarkerText() {
        return getText().trim();
    }

    @Override
    protected PsiElement createMarkerElement(String name) {
        return BashPsiElementFactory.createHeredocEndMarker(getProject(), name);
    }
}