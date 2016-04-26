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

package com.ansorgit.plugins.bash.lang.psi.impl.heredoc;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
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

    @Override
    public HeredocMarkerReference createReference() {
        return new HeredocEndMarkerReference(BashHereDocEndMarkerImpl.this);
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
    public boolean isIgnoringTabs() {
        return getNode().getElementType() == BashElementTypes.HEREDOC_END_IGNORING_TABS_ELEMENT;
    }

    private static class HeredocEndMarkerReference extends HeredocMarkerReference {
        HeredocEndMarkerReference(BashHereDocEndMarker marker) {
            super(marker);
        }

        @Nullable
        @Override
        public PsiElement resolveInner() {
            final String markerName = marker.getMarkerText();
            if (markerName == null || markerName.isEmpty()) {
                return null;
            }

            //walk to the command containing this heredoc end marker
            //fixme fix this stub?
            BashComposedCommand parent = BashPsiUtils.findParent(marker, BashComposedCommand.class);
            if (parent == null) {
                return null;
            }

            final List<BashHereDocStartMarker> startMarkers = Lists.newLinkedList();
            BashPsiUtils.visitRecursively(parent, new BashVisitor() {
                @Override
                public void visitHereDocStartMarker(BashHereDocStartMarker marker) {
                    startMarkers.add(marker);
                }
            });

            //find out which position the marker is in a list of multiple
            int markerPos = 0;
            for (PsiElement current = marker.getPrevSibling(); current != null; current = current.getPrevSibling()) {
                if (current instanceof BashHereDocEndMarker) {
                    markerPos++;
                }
            }

            return startMarkers.size() > markerPos ? startMarkers.get(markerPos) : null;
        }

        @Override
        protected PsiElement createMarkerElement(String name) {
            String markerText = getElement().getText();

            int leadingTabs = 0;
            for (int i = 0; i < markerText.length() && markerText.charAt(i) == '\t'; i++) {
                leadingTabs++;
            }

            return BashPsiElementFactory.createHeredocEndMarker(marker.getProject(), name, leadingTabs);
        }
    }
}