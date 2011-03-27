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
