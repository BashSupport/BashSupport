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

package com.ansorgit.plugins.bash.lang.psi.util;

import com.ansorgit.plugins.bash.lang.psi.BashScopeProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.google.common.collect.Iterators;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * The base class for psi processors.
 * <br>
 * @author jansorg
 */
public abstract class BashAbstractProcessor implements BashScopeProcessor, PsiScopeProcessor, ResolveProcessor {
    private final boolean preferNeigbourhood;
    private Multimap<Integer, PsiElement> results;
    private Multimap<PsiElement, PsiElement> includeCommands;

    protected BashAbstractProcessor(boolean preferNeighbourhood) {
        this.preferNeigbourhood = preferNeighbourhood;
    }

    public void handleEvent(@NotNull Event event, Object o) {
    }

    public final PsiElement getBestResult(boolean firstResult, PsiElement referenceElement) {
        return findBestResult(results, firstResult, referenceElement);
    }

    public Collection<PsiElement> getResults() {
        if (results == null) {
            return Collections.emptyList();
        }

        return results.values();
    }

    public boolean hasResults() {
        return results != null && !results.isEmpty();
    }

    protected final void removeResult(PsiElement element) {
        if (results != null && results.containsValue(element)) {
            results.values().remove(element);
        }
    }

    protected final void storeResult(PsiElement element, Integer rating, PsiElement includeCommand) {
        if (results == null) {
            results = LinkedListMultimap.create();
        }
        results.put(rating, element);

        if (includeCommand != null) {
            if (includeCommands == null) {
                includeCommands = LinkedListMultimap.create();
            }
            includeCommands.put(element, includeCommand);
        }
    }

    /**
     * Returns the best results. It takes all the elements which have been rated the best
     * and returns the first / last, depending on the parameter.
     *
     * @param results          The results to check
     * @param firstResult      If the first element of the best element list should be returned.
     * @param referenceElement
     * @return The result
     */
    private PsiElement findBestResult(Multimap<Integer, PsiElement> results, boolean firstResult, PsiElement referenceElement) {
        if (!hasResults()) {
            return null;
        }

        if (firstResult) {
            return Iterators.get(results.values().iterator(), 0);
        }

        //if the first should not be used return the best element
        int referenceLevel = preferNeigbourhood && (referenceElement != null) ? BashPsiUtils.blockNestingLevel(referenceElement) : 0;

        // find the best suitable result rating
        // The best one is as close as possible to the given referenceElement
        int bestRating = Integer.MAX_VALUE;
        int bestDelta = Integer.MAX_VALUE;
        for (int rating : results.keySet()) {
            final int delta = Math.abs(referenceLevel - rating);
            if (delta < bestDelta) {
                bestDelta = delta;
                bestRating = rating;
            }
        }

        // now get the best result
        // if there are equal definitions on the same level we prefer the first if the neighbourhood is not preferred
        if (preferNeigbourhood) {
            return Iterators.getLast(results.get(bestRating).iterator());
        } else {
            //return the element which has the lowest textOffset
            long smallestOffset = Integer.MAX_VALUE;
            PsiElement bestElement = null;

            for (PsiElement e : results.get(bestRating)) {
                //if the element is injected compute the text offset in the real file
                int textOffset = e.getTextOffset();
                if (BashPsiUtils.isInjectedElement(e)) {
                    //fixme optimize this
                    PsiLanguageInjectionHost injectionHost = InjectedLanguageManager.getInstance(e.getProject()).getInjectionHost(e);
                    if (injectionHost != null) {
                        textOffset = textOffset + injectionHost.getTextOffset();
                    }
                }

                // comparing the offset is only meaningful within the same file
                // for definitions in included files we need to compare against the offset of the include command
                Collection<PsiElement> includeCommands = this.includeCommands != null ? this.includeCommands.get(e) : Collections.emptyList();
                if (!includeCommands.isEmpty()) {
                    for (PsiElement includeCommand : includeCommands) {
                        int includeOffset = includeCommand.getTextOffset();
                        if (includeOffset < smallestOffset) {
                            smallestOffset = includeOffset;
                            bestElement = e;
                        }
                    }
                } else if (textOffset < smallestOffset) {
                    smallestOffset = textOffset;
                    bestElement = e;
                }
            }

            return bestElement;
        }
    }

    public void reset() {
        if (results != null) {
            results.clear();
        }
    }

    @Override
    public void prepareResults() {

    }
}
