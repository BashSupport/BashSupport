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

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashComposedCommand;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocStartMarker;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.lang.util.HeredocSharedImpl;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BashHereDocStartMarkerImpl extends AbstractHeredocMarker implements BashHereDocStartMarker {
    public BashHereDocStartMarkerImpl(final ASTNode astNode) {
        super(astNode, "Bash heredoc start marker");
    }

    @Override
    public boolean isIgnoringTabs() {
        return false;
    }

    @Override
    public HeredocMarkerReference createReference() {
        return new HeredocStartMarkerReference(BashHereDocStartMarkerImpl.this);
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
        return HeredocSharedImpl.isEvaluatingMarker(getText().trim());

    }

    private static class HeredocStartMarkerReference extends HeredocMarkerReference {
        HeredocStartMarkerReference(BashHereDocStartMarker marker) {
            super(marker);
        }

        @Nullable
        @Override
        public PsiElement resolveInner() {
            final String markerName = marker.getMarkerText();
            if (markerName == null || markerName.isEmpty()) {
                return null;
            }

            //walk to the command containing this heredoc start marker
            //then walk the command's siblings to return the fist matching locator
            BashComposedCommand parent = BashPsiUtils.findParent(marker, BashComposedCommand.class);
            if (parent == null) {
                return null;
            }

            final List<BashHereDocEndMarker> endMarkers = Lists.newLinkedList();
            BashPsiUtils.visitRecursively(parent, new BashVisitor() {
                @Override
                public void visitHereDocEndMarker(BashHereDocEndMarker marker) {
                    endMarkers.add(marker);
                }
            });

            //find out which position the marker is in a list of multiple, all start markers are wrapped in a single parent (a RedirectList)
            int markerPos = 0;
            for (PsiElement sibling = marker.getPrevSibling(); sibling != null; sibling = sibling.getPrevSibling()) {
                if (sibling instanceof BashHereDocMarker) {
                    markerPos++;
                }
            }

            return endMarkers.size() > markerPos ? endMarkers.get(markerPos) : null;
        }

        @Override
        protected PsiElement createMarkerElement(String name) {
            //wrap the new name in the same context as the current marker, i.e. EOF becomes "EOF" if it is a new name for "NAME"
            String newName = HeredocSharedImpl.wrapMarker(name, marker.getText());
            return BashPsiElementFactory.createHeredocStartMarker(marker.getProject(), newName);
        }
    }
}
