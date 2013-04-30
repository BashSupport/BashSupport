/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: Keys.java, Class: Keys
 * Last modified: 2013-04-30
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

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.google.common.collect.Multimap;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

/**
 * User: jansorg
 * Date: 06.02.11
 * Time: 18:27
 */
public interface Keys {
    Key<Multimap<VirtualFile, PsiElement>> visitedIncludeFiles = new Key<Multimap<VirtualFile, PsiElement>>("visitedIncludeFiles");
}
