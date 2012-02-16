/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSmartMarker.java, Class: BashSmartMarker
 * Last modified: 2010-11-23 21:32
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

package com.ansorgit.plugins.bash.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesAndCommentsBinder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

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
        delegate.drop();
        open = false;
    }

    public void rollbackTo() {
        delegate.rollbackTo();
        open = false;
    }

    public void done(IElementType type) {
        delegate.done(type);
        open = false;
    }

    public void doneBefore(IElementType type, PsiBuilder.Marker before) {
        delegate.doneBefore(type, before);
        open = false;
    }

    public void doneBefore(IElementType type, PsiBuilder.Marker before, String errorMessage) {
        delegate.doneBefore(type, before, errorMessage);
        open = false;
    }

    public void error(String message) {
        delegate.error(message);
        open = false;
    }

    public void collapse(IElementType iElementType) {
        delegate.collapse(iElementType);
    }

    public void setCustomEdgeTokenBinders(@Nullable WhitespacesAndCommentsBinder whitespacesAndCommentsBinder, @Nullable WhitespacesAndCommentsBinder whitespacesAndCommentsBinder1) {
        delegate.setCustomEdgeTokenBinders(whitespacesAndCommentsBinder, whitespacesAndCommentsBinder1);
    }

    public void errorBefore(String message, PsiBuilder.Marker marker) {
        delegate.errorBefore(message, marker);
        open = false;
    }
}
