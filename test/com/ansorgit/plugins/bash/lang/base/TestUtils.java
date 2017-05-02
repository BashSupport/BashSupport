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

package com.ansorgit.plugins.bash.lang.base;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class TestUtils {
    public static List<String> readInput(String filePath) throws IOException {
        String content = new String(FileUtil.loadFileText(new File(filePath)));
        Assert.assertNotNull(content);

        List<String> input = new ArrayList<String>();

        int separatorIndex;
        content = StringUtil.replace(content, "\r", ""); // for MACs

        // Adding input  before -----
        while ((separatorIndex = content.indexOf("-----")) >= 0) {
            input.add(content.substring(0, separatorIndex - 1));
            content = content.substring(separatorIndex);
            while (StringUtil.startsWithChar(content, '-')) {
                content = content.substring(1);
            }
            if (StringUtil.startsWithChar(content, '\n')) {
                content = content.substring(1);
            }
        }
        // Result - after -----
        if (content.endsWith("\n")) {
            content = content.substring(0, content.length() - 1);
        }
        input.add(content);

        Assert.assertTrue("No data found in source file", input.size() > 0);
        Assert.assertNotNull("Test output points to null", input.size() > 1);

        return input;
    }
}
