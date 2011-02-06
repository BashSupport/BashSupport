package com.ansorgit.plugins.bash.lang.psi.impl;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.KeyWithDefaultValue;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

import java.util.Collection;
import java.util.List;

/**
 * User: jansorg
 * Date: 06.02.11
 * Time: 18:27
 */
public interface Keys {
    final Key<Multimap<VirtualFile, PsiElement>> visitedIncludeFiles = new KeyWithDefaultValue<Multimap<VirtualFile, PsiElement>>("visitedIncludeFiles") {
        @Override
        public Multimap<VirtualFile, PsiElement> getDefaultValue() {
            return Multimaps.newListMultimap(Maps.<VirtualFile, Collection<PsiElement>>newHashMap(), new Supplier<List<PsiElement>>() {
                public List<PsiElement> get() {
                    return Lists.newLinkedList();
                }
            });
        }
    };
}
