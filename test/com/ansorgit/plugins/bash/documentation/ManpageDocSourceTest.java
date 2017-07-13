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

package com.ansorgit.plugins.bash.documentation;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ManpageDocSourceTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testExternalUrl() throws Exception {
        assertValidManPage("curl");
        assertValidManPage("wget");
        assertValidManPage("info");
        assertValidManPage("man");
        assertValidManPage("make");
        assertValidManPage("gcc");

//        assertMissingManPage("abcdefghi");
    }

    private void assertValidManPage(String commandName) {
        Assert.assertTrue("Expected valid url content for " + commandName, isValidManPageContent(commandName));
    }

    private void assertMissingManPage(String commandName) {
        Assert.assertFalse("Expected missing url content for " + commandName, isValidManPageContent(commandName));
    }

    private boolean isValidManPageContent(String commandName) {
        ManpageDocSource source = new ManpageDocSource();

        BashCommand command = PsiTreeUtil.findChildOfType(createLightFile(BashFileType.BASH_FILE_TYPE, commandName), BashCommand.class);
        Assert.assertTrue(source.isValid(command, command));

        String url = source.documentationUrl(command, command);
        return DocTestUtils.isResponseContentValid(url);
    }
}