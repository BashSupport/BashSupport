/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSmartMarker.java, Class: BashSmartMarker
 * Last modified: 2010-10-05
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

package com.ansorgit.plugins.bash.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesAndCommentsProcessor;
import com.intellij.psi.tree.IElementType;

/**
 * Wraps a PsiBuilder marker and keeps track whether it's still open or not.
 * <p/>
 * User: jansorg
 * Date: Jan 30, 2010
 * Time: 12:18:29 PM
 */
public final class BashSmartMarker implements PsiBuilder.Marker {
    private PsiBuilder.Marker delegate;
    private boolean open = true;

    public BashSmartMarker(PsiBuilder.Marker delegate) {
        this.delegate = delegate;
    }

    public boolean isOpen() {
        return open;
    }

    public PsiBuilder.Marker precede() {
        return delegate.precede();
    }

    public void drop() {
        open = false;
        delegate.drop();
    }

    public void rollbackTo() {
        open = false;
        delegate.rollbackTo();
    }

    public void done(IElementType type) {
        open = false;
        delegate.done(type);
    }

    public void doneBefore(IElementType type, PsiBuilder.Marker before) {
        open = false;
        delegate.doneBefore(type, before);
    }

    public void doneBefore(IElementType type, PsiBuilder.Marker before, String errorMessage) {
        open = false;
        delegate.doneBefore(type, before, errorMessage);
    }

    public void error(String message) {
        open = false;
        delegate.error(message);
    }

    public void collapse(IElementType iElementType) {
        delegate.collapse(iElementType);
    }

    public void setCustomEdgeProcessors(WhitespacesAndCommentsProcessor first, WhitespacesAndCommentsProcessor second) {
        delegate.setCustomEdgeProcessors(first, second);
    }

    public void errorBefore(String s, PsiBuilder.Marker marker) {
        open = false;
        delegate.errorBefore(s, marker);
    }
}
