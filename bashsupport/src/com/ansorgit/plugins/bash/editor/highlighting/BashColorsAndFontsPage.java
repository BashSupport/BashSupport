/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashColorsAndFontsPage.java, Class: BashColorsAndFontsPage
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

package com.ansorgit.plugins.bash.editor.highlighting;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class BashColorsAndFontsPage implements ColorSettingsPage {
    @NotNull
    public String getDisplayName() {
        return "Bash";
    }

    @Nullable
    public Icon getIcon() {
        return BashIcons.BASH_FILE_ICON;
    }

    @NotNull
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRS;
    }

    private static final AttributesDescriptor[] ATTRS =
            new AttributesDescriptor[]{
                    new AttributesDescriptor(BashSyntaxHighlighter.LINE_COMMENT_ID, BashSyntaxHighlighter.LINE_COMMENT),
                    new AttributesDescriptor(BashSyntaxHighlighter.SHEBANG_ID, BashSyntaxHighlighter.SHEBANG_COMMENT),
                    new AttributesDescriptor(BashSyntaxHighlighter.KEYWORD_ID, BashSyntaxHighlighter.KEYWORD),
                    new AttributesDescriptor(BashSyntaxHighlighter.PAREN_ID, BashSyntaxHighlighter.PAREN),
                    new AttributesDescriptor(BashSyntaxHighlighter.BRACES_ID, BashSyntaxHighlighter.BRACE),
                    new AttributesDescriptor(BashSyntaxHighlighter.BRACKETS_ID, BashSyntaxHighlighter.BRACKET),
                    new AttributesDescriptor(BashSyntaxHighlighter.STRING2_ID, BashSyntaxHighlighter.STRING2),
                    new AttributesDescriptor(BashSyntaxHighlighter.NUMBER_ID, BashSyntaxHighlighter.NUMBER),
                    new AttributesDescriptor(BashSyntaxHighlighter.REDIRECTION_ID, BashSyntaxHighlighter.REDIRECTION),
                    new AttributesDescriptor(BashSyntaxHighlighter.CONDITIONAL_ID, BashSyntaxHighlighter.CONDITIONAL),
                    new AttributesDescriptor(BashSyntaxHighlighter.INTERNAL_COMMAND_ID, BashSyntaxHighlighter.INTERNAL_COMMAND),
                    new AttributesDescriptor(BashSyntaxHighlighter.VAR_USE_ID, BashSyntaxHighlighter.VAR_USE),
                    new AttributesDescriptor(BashSyntaxHighlighter.BAD_CHARACTER_ID, BashSyntaxHighlighter.BAD_CHARACTER),

                    //PSI highlighting
                    new AttributesDescriptor(BashSyntaxHighlighter.STRING_ID, BashSyntaxHighlighter.STRING),
                    new AttributesDescriptor(BashSyntaxHighlighter.HERE_DOC_ID, BashSyntaxHighlighter.HERE_DOC),
                    new AttributesDescriptor(BashSyntaxHighlighter.HERE_DOC_START_ID, BashSyntaxHighlighter.HERE_DOC_START),
                    new AttributesDescriptor(BashSyntaxHighlighter.HERE_DOC_END_ID, BashSyntaxHighlighter.HERE_DOC_END),
                    new AttributesDescriptor(BashSyntaxHighlighter.BACKQUOTE_COMMAND_ID, BashSyntaxHighlighter.BACKQUOTE),
                    new AttributesDescriptor(BashSyntaxHighlighter.EXTERNAL_COMMAND_ID, BashSyntaxHighlighter.EXTERNAL_COMMAND),
                    new AttributesDescriptor(BashSyntaxHighlighter.SUBSHELL_COMMAND_ID, BashSyntaxHighlighter.SUBSHELL_COMMAND),
                    new AttributesDescriptor(BashSyntaxHighlighter.FUNCTION_CALL_ID, BashSyntaxHighlighter.FUNCTION_CALL),
                    new AttributesDescriptor(BashSyntaxHighlighter.VAR_DEF_ID, BashSyntaxHighlighter.VAR_DEF),
                    new AttributesDescriptor(BashSyntaxHighlighter.VAR_USE_BUILTIN_ID, BashSyntaxHighlighter.VAR_USE_BUILTIN),
                    new AttributesDescriptor(BashSyntaxHighlighter.VAR_USE_COMPOSED_ID, BashSyntaxHighlighter.VAR_USE_COMPOSED),
            };

    @NotNull
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    public SyntaxHighlighter getHighlighter() {
        return SyntaxHighlighter.PROVIDER.create(BashFileType.BASH_FILE_TYPE, null, null);
    }

    @NonNls
    @NotNull
    public String getDemoText() {
        return "#!/bin/sh\n\n" +
                "<internalCmd>export</internalCmd> <varDef>subject</varDef>=world\n" +
                "echo \"Hello $<internalVar>subject</internalVar>\"\n" +
                "\n" +
                "# Function to greet someone\n" +
                "function greetingTo() {\n" +
                "   <internalCmd>local</internalCmd> <varDef>mySubject</varDef>=$1\n" +
                "   [ -z $mySubject ] || <externalCmd>cat</externalCmd> - <<dummy><</dummy><heredocStart>EOF</heredocStart>\n" +
                "<heredoc>   Have a look at this great here doc, $<internalVar>mySubject</internalVar>!</heredoc>\n" +
                "<heredocEnd>EOF</heredocEnd>\n" +
                "}\n" +
                "\n" +
                "<functionCall>greetingTo</functionCall> 'World';\n" +
                "\n" +
                "for n in $<subshellCmd>(<externalCmd>seq</externalCmd> 3 10)</subshellCmd>; do\n" +
                "   echo <string>\"1 + 2+...+<internalVar>$n</internalVar> = $((<internalVar>$n</internalVar>*(<internalVar>$n</internalVar>+1)/2))\"</string>\n" +
                "done\n" +
                "\n" +
                "<internalCmd>echo</internalCmd> <string>\"27+15=<backquote>`<internalCmd>echo</internalCmd> 42`</backquote>\"</string>;\n" +
                "\n" +
                "${<internalVar>subject</internalVar>:1:2}\n";
    }

    @Nullable
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        final Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
        map.put("string", BashSyntaxHighlighter.STRING);
        map.put("heredoc", BashSyntaxHighlighter.HERE_DOC);
        map.put("heredocStart", BashSyntaxHighlighter.HERE_DOC_START);
        map.put("heredocEnd", BashSyntaxHighlighter.HERE_DOC_END);
        map.put("backquote", BashSyntaxHighlighter.BACKQUOTE);
        map.put("internalCmd", BashSyntaxHighlighter.INTERNAL_COMMAND);
        map.put("externalCmd", BashSyntaxHighlighter.EXTERNAL_COMMAND);
        map.put("subshellCmd", BashSyntaxHighlighter.SUBSHELL_COMMAND);
        map.put("functionCall", BashSyntaxHighlighter.FUNCTION_CALL);
        map.put("varDef", BashSyntaxHighlighter.VAR_DEF);
        map.put("internalVar", BashSyntaxHighlighter.VAR_USE_BUILTIN);
        map.put("composedVar", BashSyntaxHighlighter.VAR_USE_COMPOSED);

        // we need this to be able to insert << in the text
        // (relies on the current implementation of JetBrain's HighlightsExtractor class)
        map.put("dummy", TextAttributesKey.find("dummy"));
        return map;
    }
}
