/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashResolveUtil.java, Class: BashResolveUtil
 * Last modified: 2009-12-04
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

package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains several methods to walk through a PSI tree.
 * <p/>
 * Date: 12.04.2009
 * Time: 23:33:57
 *
 * @author Joachim Ansorg
 */
public class BashResolveUtil {
    private static final Logger log = Logger.getInstance("#bash.BashResolveUtil");

    /**
     * Walks through a PSI tree by going upstream. It may walk into the children of earlier items.
     * It also allows to use the first found result or the result which is at the topmost position in the tree.
     *
     * @param processor           The resolve processor to use.
     * @param elt                 The
     * @param lastParent          The last parent element. Is used to ignore the siblings of the current position.
     * @param place               The place where we start the resolve process.
     * @param useFirstResult      If true the topmost result will be used. Otherwise the result which is the nearest to the start will be returned. The exact behaviour depends on the processor.
     * @param ignoreParentContext If true the parent of the start will be ignored.
     * @return The psi element which conforms to the result definition or null, of nothing has been found.
     */
    @Nullable
    public static PsiElement treeWalkUp(final ResolveProcessor processor, final PsiElement elt, final PsiElement lastParent, PsiElement place, boolean useFirstResult, boolean ignoreParentContext) {
        //parent context may only be ignored for walk-down in the tree
        walkThrough(processor, elt, lastParent, place, true, false);
        if (!processor.hasResults()) {
            //move downwards
            walkThrough(processor, elt, lastParent, place, false, ignoreParentContext);
        }

        return processor.hasResults() ? processor.getBestResult(useFirstResult, elt) : null;
    }

    /**
     * Walsk through the PSI tree looking for an element accepted by the processor.
     *
     * @param processor           The processor which is used to check each item
     * @param elt                 The start node
     * @param lastParent
     * @param place
     * @param moveUp
     * @param ignoreParentContext
     */
    public static void walkThrough(final ResolveProcessor processor, final PsiElement elt, final PsiElement lastParent, PsiElement place, boolean moveUp, boolean ignoreParentContext) {
        if (elt == null) return;
        final Set<PsiElement> ignoreList = ignoreParentContext ? setupIgnoreList(elt) : Collections.<PsiElement>emptySet();

        PsiElement cur;
        if (!moveUp) {
            //if we're moving down start with the first sibling of the closest sourrounding block
            final PsiElement enclosingBlock = BashPsiUtils.findEnclosingBlock(elt);
            cur = BashPsiUtils.elementAfter(enclosingBlock);
        } else {
            cur = elt;
        }

        if (cur == null) {
            return;
        }

        do {
            //check the current item
            cur.processDeclarations(processor, ResolveState.initial(), cur == elt ? lastParent : null, place);

            if (cur instanceof PsiFile) break;

            //look for the next element which is not in the ignore list
            do {
                final PsiElement lastGood = cur;

                cur = moveUp ? cur.getPrevSibling() : cur.getNextSibling();
                if (cur == null) { //first or last element in the context
                    cur = lastGood.getContext();
                }
            } while (cur != null && ignoreList.contains(cur));
        } while (cur != null);
    }

    private static Set<PsiElement> setupIgnoreList(PsiElement elt) {
        Set<PsiElement> ignoreList = new HashSet<PsiElement>();
        ignoreList.add(elt);

        PsiElement cur = elt;
        while (cur.getContext() != null) {
            cur = cur.getContext();
            ignoreList.add(cur);
        }

        return ignoreList;
    }
}
