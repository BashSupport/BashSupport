/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ForwardingMarker.java, Class: ForwardingMarker
 * Last modified: 2010-03-24
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

package com.ansorgit.plugins.bash.lang.parser.util;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * A forwarding marker implementation which is useful for enhancements which
 * are based on aggregation.
 * <p/>
 * Date: 17.04.2009
 * Time: 16:56:16
 *
 * @author Joachim Ansorg
 */
public abstract class ForwardingMarker implements PsiBuilder.Marker {
    protected final PsiBuilder.Marker original;

    protected ForwardingMarker(PsiBuilder.Marker original) {
        this.original = original;
    }

    public void done(IElementType type) {
        original.done(type);
    }

    public void doneBefore(IElementType type, PsiBuilder.Marker before) {
        original.doneBefore(type, before);
    }

    public void doneBefore(IElementType type, PsiBuilder.Marker before, String errorMessage) {
        original.doneBefore(type, before, errorMessage);
    }

    public void drop() {
        original.drop();
    }

    public void error(String message) {
        original.error(message);
    }

    public PsiBuilder.Marker precede() {
        return original.precede();
    }

    public void rollbackTo() {
        original.rollbackTo();
    }
}
