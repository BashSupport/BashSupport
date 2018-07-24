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
import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.google.common.collect.Sets;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

/**
 */
public class InternalCommandDocumentationTest extends LightBashCodeInsightFixtureTestCase {

    @Test
    public void testResourceNameForElement() throws Exception {
        BashCommand element = findFirstCommand("source abc.bash");

        InternalCommandDocumentation source = new InternalCommandDocumentation();
        Assert.assertEquals("source", source.resourceNameForElement(element));
    }

    @Test
    public void testIsValid() throws Exception {
        Assert.assertTrue(doIsValidCommand("source abc.bash"));
        Assert.assertTrue(doIsValidCommand("logout"));
        Assert.assertTrue(doIsValidCommand("kill"));

        Assert.assertFalse(doIsValidCommand("curl"));
        Assert.assertFalse(doIsValidCommand("wget"));
    }

    private boolean doIsValidCommand(String content) {
        BashCommand element = findFirstCommand(content);

        return new InternalCommandDocumentation().isValid(element, element);
    }

    @Test
    public void testDocumentationUrl() throws Exception {
        Assert.assertTrue(doIsValidUrlContent("source abc.bash"));
        Assert.assertTrue(doIsValidUrlContent("kill"));
        Assert.assertTrue(doIsValidUrlContent("jobs"));

        Assert.assertFalse(doIsValidUrlContent("curl"));
        Assert.assertFalse(doIsValidUrlContent("wget"));

        Set<String> unavailableCommandDocs = Sets.newHashSet("readarray", "mapfile");

        //check all builtin commands
        InternalCommandDocumentation source = new InternalCommandDocumentation();
        for (String command : filterUnavailableCommands(LanguageBuiltins.commands)) {
            if (!unavailableCommandDocs.contains(command)) {
                DocTestUtils.isResponseContentValid(source.urlForCommand(command));
            }
        }

        for (String command : filterUnavailableCommands(LanguageBuiltins.commands_v4)) {
            if (!unavailableCommandDocs.contains(command)) {
                DocTestUtils.isResponseContentValid(source.urlForCommand(command));
            }
        }
    }

    @Test
    public void testIssue339() throws Exception {
        InternalCommandDocumentation source = new InternalCommandDocumentation();

        PsiFile file = createLightFile(BashFileType.BASH_FILE_TYPE, "echo hello world");
        Assert.assertNull(source.documentationUrl(file, file));
    }

    private Collection<String> filterUnavailableCommands(Collection<String> commands) {
        Set<String> copy = Sets.newHashSet(commands);
        copy.remove(".");
        copy.remove("help");
        copy.remove("trap");
        copy.remove("compgen");
        copy.remove(";");
        copy.remove(":");
        copy.remove("caller");
        copy.remove("complete");
        copy.remove("typeset");
        copy.remove("fc");
        copy.remove("disown");
        copy.remove("bind");
        copy.remove("coproc");
        copy.remove("mapfile");
        return copy;
    }

    private boolean doIsValidUrlContent(String content) {
        BashCommand element = findFirstCommand(content);
        InternalCommandDocumentation source = new InternalCommandDocumentation();
        if (!source.isValid(element, element)) {
            return false;
        }

        String url = source.documentationUrl(element, element);
        Assert.assertEquals("Expected a valid external url", String.format("https://ss64.com/bash/%s.html", element.getReferencedCommandName()), url);

        return DocTestUtils.isResponseContentValid(url);
    }

    @NotNull
    private BashCommand findFirstCommand(String content) {
        BashCommand element = PsiTreeUtil.findChildOfType(createLightFile(BashFileType.BASH_FILE_TYPE, content), BashCommand.class);
        Assert.assertNotNull(element);

        return element;
    }
}