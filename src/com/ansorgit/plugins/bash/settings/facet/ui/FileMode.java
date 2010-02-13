/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: FileMode.java, Class: FileMode
 * Last modified: 2010-02-13
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

package com.ansorgit.plugins.bash.settings.facet.ui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * User: jansorg
 * Date: Feb 12, 2010
 * Time: 10:36:46 PM
 */
public final class FileMode implements Comparable<FileMode> {
    private final String id;
    private final String displayName;

    private static Map<String, FileMode> idMap = Maps.newHashMap();

    static {
        idMap.put("accept", new FileMode("accept", "Accept"));
        idMap.put("ignore", new FileMode("ignore", "Ignore"));
        idMap.put("guess", new FileMode("guess", "Auto"));
    }

    public static List<FileMode> all() {
        return Lists.newArrayList(auto(), accept(), ignore());
    }

    public static FileMode accept() {
        return idMap.get("accept");
    }

    public static FileMode auto() {
        return idMap.get("guess");
    }

    public static FileMode ignore() {
        return idMap.get("ignore");
    }

    public static FileMode forId(String id) {
        return idMap.get(id);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    private FileMode(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }


    public int compareTo(FileMode o) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
