/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: NullMarker.java, Class: NullMarker
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

package com.ansorgit.plugins.bash.util;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesAndCommentsBinder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

/**
 * Marker which does nothing
 */
public class NullMarker implements PsiBuilder.Marker {
    private static PsiBuilder.Marker instance = new NullMarker();

    public static PsiBuilder.Marker get() {
        return instance;
    }

    public PsiBuilder.Marker precede() {
        return null;
    }

    public void drop() {

    }

    public void rollbackTo() {

    }

    public void done(IElementType iElementType) {

    }

    public void collapse(IElementType iElementType) {

    }

    public void doneBefore(IElementType iElementType, PsiBuilder.Marker marker) {

    }

    public void doneBefore(IElementType iElementType, PsiBuilder.Marker marker, String s) {

    }

    public void error(String s) {

    }

    public void errorBefore(String s, PsiBuilder.Marker marker) {
    }

    public void setCustomEdgeTokenBinders(@Nullable WhitespacesAndCommentsBinder whitespacesAndCommentsBinder, @Nullable WhitespacesAndCommentsBinder whitespacesAndCommentsBinder1) {
    }
}
