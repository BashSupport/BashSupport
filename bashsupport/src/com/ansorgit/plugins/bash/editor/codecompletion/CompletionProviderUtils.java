package com.ansorgit.plugins.bash.editor.codecompletion;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiNamedElement;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

/**
 * User: jansorg
 * Date: 07.02.11
 * Time: 18:38
 */
public class CompletionProviderUtils {
    private CompletionProviderUtils() {
    }

    static Collection<LookupElement> createPsiItems(Collection<? extends PsiNamedElement> elements) {
        return Collections2.transform(elements, new Function<PsiNamedElement, LookupElement>() {
            public LookupElement apply(PsiNamedElement from) {
                return LookupElementBuilder.create(from).setCaseSensitive(true);
            }
        });
    }

    static Collection<LookupElement> createItems(Collection<String> globalVars, final Icon icon) {
        return Lists.transform(Lists.newArrayList(globalVars), new Function<String, LookupElement>() {
            public LookupElement apply(String from) {
                return LookupElementBuilder.create(from).setCaseSensitive(true).setIcon(icon);
            }
        });
    }

    static Collection<LookupElement> wrapInGroup(final int groupId, Collection<LookupElement> elements) {
        return Collections2.transform(elements, new Function<LookupElement, LookupElement>() {
            public LookupElement apply(LookupElement lookupElement) {
                return PrioritizedLookupElement.withGrouping(lookupElement, groupId);
            }
        });
    }

    static Collection<LookupElement> createPathItems(List<String> paths) {
        Function<String, LookupElement> transformationFunction = new Function<String, LookupElement>() {
            public LookupElement apply(String path) {
                return new PathLookupElement(path, !path.endsWith("/"));
            }
        };

        Predicate<String> isRelativePath = new Predicate<String>() {
            public boolean apply(String path) {
                return !path.startsWith("/");
            }
        };

        Collection<String> relativePaths = Collections2.filter(paths, isRelativePath);
        Collection<LookupElement> relativePathItems = Collections2.transform(relativePaths, transformationFunction);

        Collection<String> absolutePaths = Collections2.filter(paths, Predicates.not(isRelativePath));
        Collection<LookupElement> absolutePathItems = Collections2.transform(absolutePaths, transformationFunction);

        Collection<LookupElement> result = Lists.newLinkedList();
        result.addAll(wrapInGroup(CompletionGrouping.RelativeFilePath.ordinal(), relativePathItems));
        result.addAll(wrapInGroup(CompletionGrouping.AbsoluteFilePath.ordinal(), absolutePathItems));

        return result;
    }
}
