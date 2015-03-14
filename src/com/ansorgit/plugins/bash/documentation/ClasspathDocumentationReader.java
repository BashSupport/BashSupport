/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ClasspathDocumentationReader.java, Class: ClasspathDocumentationReader
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

package com.ansorgit.plugins.bash.documentation;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.util.text.StringUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Helper class to read documentation from a url source.
 * <p/>
 * Date: 03.05.2009
 * Time: 18:30:19
 *
 * @author Joachim Ansorg
 */
class ClasspathDocumentationReader {
    private static final Logger log = Logger.getInstance("#bash.DocumentationReader");

    private ClasspathDocumentationReader() {
    }

    /**
     * Reads documenation from a url, mostly this is a file source.
     *
     * @param path    The prefix path to use.
     * @param command The command name, e.g. "echo"
     * @return The documentation content or null.
     */
    static String readFromClasspath(String path, String command) {
        //log.debug("loading doc for " + path + "/" + command + ".txt");
        if (StringUtil.isEmpty(path) || StringUtil.isEmpty(command)) {
            return null;
        }

        final String fullPath = path + "/" + command + ".html";
        try {
            URL url = ClasspathDocumentationReader.class.getResource(fullPath);
            if (url == null) {
                log.debug("couldn't find resource");
                return null;
            }

            final InputStream inputStream = new BufferedInputStream(url.openStream());

            return StreamUtil.readText(inputStream);
        } catch (IOException e) {
            log.debug("Failed to read documentation.", e);
        }

        return null;
    }
}
