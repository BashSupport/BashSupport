/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ForwardingPsiBuilder.java, Class: ForwardingPsiBuilder
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

import com.intellij.lang.ASTNode;
import com.intellij.lang.ITokenTypeRemapper;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.util.Key;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.diff.FlyweightCapableTreeStructure;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

/**
 * A PsiBuilder implementation which delegates all method to another PsiBuilder.
 * This is helpful for enhancements to PsiBuilder which base on an existing psi builder.
 * <p/>
 * Date: 17.04.2009
 * Time: 16:36:43
 *
 * @author Joachim Ansorg
 */
public abstract class ForwardingPsiBuilder implements PsiBuilder {
    private final PsiBuilder originalPsiBuilder;

    public ForwardingPsiBuilder(PsiBuilder originalPsiBuilder) {
        this.originalPsiBuilder = originalPsiBuilder;
    }

    public PsiBuilder getOriginalPsiBuilder() {
        return originalPsiBuilder;
    }

    public void advanceLexer() {
        originalPsiBuilder.advanceLexer();
    }

    public void enforceCommentTokens(TokenSet tokens) {
        originalPsiBuilder.enforceCommentTokens(tokens);
    }

    public boolean eof() {
        return originalPsiBuilder.eof();
    }

    public void error(String messageText) {
        originalPsiBuilder.error(messageText);
    }

    public int getCurrentOffset() {
        return originalPsiBuilder.getCurrentOffset();
    }

    public FlyweightCapableTreeStructure<LighterASTNode> getLightTree() {
        return originalPsiBuilder.getLightTree();
    }

    public CharSequence getOriginalText() {
        return originalPsiBuilder.getOriginalText();
    }

    @Nullable
    @NonNls
    public String getTokenText() {
        return originalPsiBuilder.getTokenText();
    }

    @Nullable
    public IElementType getTokenType() {
        return originalPsiBuilder.getTokenType();
    }

    public ASTNode getTreeBuilt() {
        return originalPsiBuilder.getTreeBuilt();
    }

    public Marker mark() {
        return originalPsiBuilder.mark();
    }

    public void setDebugMode(boolean dbgMode) {
        originalPsiBuilder.setDebugMode(dbgMode);
    }

    public void setTokenTypeRemapper(ITokenTypeRemapper remapper) {
        originalPsiBuilder.setTokenTypeRemapper(remapper);
    }

    public <T> T getUserData(Key<T> key) {
        return originalPsiBuilder.getUserData(key);
    }

    public <T> void putUserData(Key<T> key, T value) {
        originalPsiBuilder.putUserData(key, value);
    }

    //fixme this is an API change by JetBrains added in a 9.0.2 eap
    //fixme delegating is not compatible with newer sdks.

    /*public LighterASTNode getLatestDoneMarker() {
        return originalPsiBuilder.getLatestDoneMarker();
    } */
}
