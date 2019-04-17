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

package com.ansorgit.plugins.bash.editor;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jansorg
 */
public class EnterInStringLiteralHandlerTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testEnterInCommand() {
        CodeStyleSettingsManager.getInstance(getProject()).getTemporarySettings().WRAP_ON_TYPING = CommonCodeStyleSettings.WrapOnTyping.WRAP.intValue;

        // 125 chars, longer than the default right margin
        myFixture.configureByText(BashFileType.BASH_FILE_TYPE, StringUtil.repeat("a", 125));
        myFixture.getEditor().getCaretModel().moveToOffset(121);
        myFixture.type('\n');

        Assert.assertEquals("Expected that line continuation was inserted",
                StringUtil.repeat("a", 121) + "\\\naaaa", myFixture.getEditor().getDocument().getText());
    }

    @Test
    public void testEnterInStringLiteratl() {
        CodeStyleSettingsManager.getInstance(getProject()).getTemporarySettings().WRAP_ON_TYPING = CommonCodeStyleSettings.WrapOnTyping.WRAP.intValue;

        // 125 chars, longer than the default right margin
        myFixture.configureByText(BashFileType.BASH_FILE_TYPE, "\"" + StringUtil.repeat("a", 125) + "\"");
        myFixture.getEditor().getCaretModel().moveToOffset(122); // opening quote + 121
        myFixture.type('\n');

        Assert.assertEquals("Expected that line continuation was inserted on enter in a string literal",
                "\"" + StringUtil.repeat("a", 121) + "\\\naaaa\"", myFixture.getEditor().getDocument().getText());
    }

    @Test
    public void testEnterAtEOLString() {
        CodeStyleSettingsManager.getInstance(getProject()).getTemporarySettings().WRAP_ON_TYPING = CommonCodeStyleSettings.WrapOnTyping.WRAP.intValue;

        // 125 chars, longer than the default right margin
        String content = "\"" + StringUtil.repeat("a", 125) + "\"";
        myFixture.configureByText(BashFileType.BASH_FILE_TYPE, content);
        myFixture.getEditor().getCaretModel().moveToOffset(content.length());
        myFixture.type('\n');

        Assert.assertEquals("Expected that line continuation wasn't inserted at the end of a line",
                content + "\n", myFixture.getEditor().getDocument().getText());
    }

    @Test
    public void testEnterAtEOLCommand() {
        CodeStyleSettingsManager.getInstance(getProject()).getTemporarySettings().WRAP_ON_TYPING = CommonCodeStyleSettings.WrapOnTyping.WRAP.intValue;

        // 125 chars, longer than the default right margin
        String content = StringUtil.repeat("a", 125);
        myFixture.configureByText(BashFileType.BASH_FILE_TYPE, content);
        myFixture.getEditor().getCaretModel().moveToOffset(content.length());
        myFixture.type('\n');

        Assert.assertEquals("Expected that line continuation isn't inserted at the end of a line",
                content + "\n", myFixture.getEditor().getDocument().getText());
    }
}