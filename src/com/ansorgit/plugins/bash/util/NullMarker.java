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

package com.ansorgit.plugins.bash.util;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesAndCommentsBinder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Marker which does nothing
 */
public final class NullMarker implements PsiBuilder.Marker {
    private static final PsiBuilder.Marker instance = new NullMarker();

    private NullMarker() {
    }

    public static PsiBuilder.Marker get() {
        return instance;
    }

    @NotNull
    public PsiBuilder.Marker precede() {
        return this;
    }

    public void drop() {
    }

    public void rollbackTo() {
    }

    public void done(@NotNull IElementType iElementType) {
    }

    public void collapse(@NotNull IElementType iElementType) {
    }

    public void doneBefore(@NotNull IElementType iElementType, @NotNull PsiBuilder.Marker marker) {
    }

    public void doneBefore(@NotNull IElementType iElementType, @NotNull PsiBuilder.Marker marker, String s) {
    }

    public void error(String s) {
    }

    public void errorBefore(String s, @NotNull PsiBuilder.Marker marker) {
    }

    public void setCustomEdgeTokenBinders(@Nullable WhitespacesAndCommentsBinder whitespacesAndCommentsBinder, @Nullable WhitespacesAndCommentsBinder whitespacesAndCommentsBinder1) {
    }
}
