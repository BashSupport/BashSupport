/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPathCommandCompletion.java, Class: BashPathCommandCompletion
 * Last modified: 2013-02-03
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

package com.ansorgit.plugins.bash.editor.codecompletion;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 */
public class BashPathCommandCompletion implements ApplicationComponent {
    public static BashPathCommandCompletion getInstance() {
        return ApplicationManager.getApplication().getComponent(BashPathCommandCompletion.class);
    }

    //manages the list of executables found in a given path prefix
    LoadingCache<String, List<File>> pathCache = CacheBuilder.newBuilder().expireAfterAccess(15, TimeUnit.MINUTES).build(new StringListCacheLoader());

    private List<String> environmentPaths = Collections.emptyList();

    @Override
    public void initComponent() {
        String envPath = System.getenv("PATH");
        if (envPath != null) {
            String[] split = envPath.split(":");
            if (split != null) {
                environmentPaths = Arrays.asList(split);
            }
        }
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Bash $PATH command completion";
    }

    public List<File> findCommands(String currentText) throws ExecutionException {
        List<File> result = Lists.newLinkedList();

        for (String prefix : environmentPaths) {
            List<File> files = pathCache.get(prefix);
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith(currentText)) {
                        result.add(file);
                    }
                }
            }
        }

        return result;
    }

    private static final class ExecutableFileFilter implements FileFilter {
        ExecutableFileFilter() {
        }

        @Override
        public boolean accept(File file) {
            return file.isFile() && file.canExecute() && file.canRead();
        }
    }

    private static final class StringListCacheLoader extends CacheLoader<String, List<File>> {
        StringListCacheLoader() {
        }

        @Override
        public List<File> load(String pathName) throws Exception {
            File path = new File(pathName);
            File[] executables = path.listFiles(new ExecutableFileFilter());

            return executables != null ? Arrays.asList(executables) : Collections.<File>emptyList();
        }
    }
}
