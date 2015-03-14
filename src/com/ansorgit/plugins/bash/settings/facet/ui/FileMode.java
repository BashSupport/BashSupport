/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: FileMode.java, Class: FileMode
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.settings.facet.ui;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FileMode controls how a single file is handled.
 * <p/>
 * User: jansorg
 * Date: Feb 12, 2010
 * Time: 10:36:46 PM
 */
public final class FileMode implements Comparable<FileMode> {
    private final String id;
    private final int index;
    private final String displayName;

    private static Map<String, FileMode> idMap = new ConcurrentHashMap<String, FileMode>();
    //default mode means that the file inherits from the parent if possible. If the parent has no
    //value set then the using class has to decide, usually this means to ignore it.
    private static final FileMode defaultMode = new FileMode("defaultMode", "Default", 0);
    //Guess the file type by content
    private static final FileMode autoMode = new FileMode("auto", "Auto (guess file type)", 1);
    //Ignore the file
    private static final FileMode ignoreMode = new FileMode("ignore", "Ignore", 2);
    //Accept a file as bash content
    private static final FileMode acceptMode = new FileMode("accept", "Accept", 3);

    static {
        idMap.put(defaultMode.id, defaultMode);
        idMap.put(autoMode.id, autoMode);
        idMap.put(ignoreMode.id, ignoreMode);
        idMap.put(acceptMode.id, acceptMode);
    }

    public static List<FileMode> all() {
        return Lists.newArrayList(auto(), accept(), ignore());
    }

    public static FileMode defaultMode() {
        return defaultMode;
    }

    public static FileMode accept() {
        return acceptMode;
    }

    public static FileMode ignore() {
        return ignoreMode;
    }

    public static FileMode auto() {
        return autoMode;
    }

    public static FileMode forId(@NotNull String id) {
        return idMap.get(id);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    private FileMode(String id, String displayName, int index) {
        this.id = id;
        this.displayName = displayName;
        this.index = index;
    }

    @Override
    public String toString() {
        return "FileMode{" +
                "displayName='" + displayName + '\'' +
                '}';
    }

    public int compareTo(FileMode o) {
        return index - o.index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileMode mode = (FileMode) o;
        return id != null ? id.equals(mode.id) : mode.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
