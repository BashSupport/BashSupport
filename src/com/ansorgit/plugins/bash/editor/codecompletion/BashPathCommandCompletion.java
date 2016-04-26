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

package com.ansorgit.plugins.bash.editor.codecompletion;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * Implements cached lookup of the global commands which are available in the configured paths of $PATH.
 *
 * @author jansorg
 */
public class BashPathCommandCompletion implements ApplicationComponent {
    //may only be modified in the initComponent method, because it's not serialzed
    private final TreeSet<String> cachedCommands = new TreeSet<String>();

    public static BashPathCommandCompletion getInstance() {
        return ApplicationManager.getApplication().getComponent(BashPathCommandCompletion.class);
    }

    @Override
    public void initComponent() {
        String envPath = System.getenv("PATH");
        if (envPath != null) {
            String[] split = StringUtils.split(envPath, ':');
            if (split != null) {
                //fixme better do this in a background task?
                for (String path : Arrays.asList(split)) {
                    File dir = new File(path);
                    if (dir.exists() && dir.isDirectory()) {
                        File[] commands = dir.listFiles(new ExecutableFileFilter());

                        if (commands != null) {
                            for (File command : commands) {
                                cachedCommands.add(command.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Bash $PATH command completion component";
    }

    public Iterable<String> findCommands(String commandPrefix) {
        return cachedCommands.subSet(commandPrefix, findUpperLimit(commandPrefix));
    }

    /**
     * Find the upper limit of the TreeSet map lookup. E.g. "git" has a upper lookup limit of "giu" (exclusive).
     *
     * @param prefix The prefix which should be used to retrieve all keys which start with this value
     * @return The key to use for the upper limit l
     */
    protected String findUpperLimit(String prefix) {
        if (prefix.isEmpty()) {
            return "z";
        }

        if (prefix.length() == 1) {
            char c = prefix.charAt(0);
            return c < 'z' ? Character.toString((char) (c + 1)) : "z";
        }

        //change the last character to 'z' to create the lookup range
        //if it already is 'z' then cut it of and call again with the substring
        char lastChar = prefix.charAt(prefix.length() - 1);
        if (lastChar < 'z') {
            return prefix.substring(0, prefix.length() - 1) + Character.toString((char) (lastChar + 1));
        }

        return findUpperLimit(prefix.substring(0, prefix.length() - 1));
    }

    private static final class ExecutableFileFilter implements FileFilter {
        ExecutableFileFilter() {
        }

        @Override
        public boolean accept(File file) {
            return file.isFile() && file.canExecute() && file.canRead();
        }
    }
}
