/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashColorsAndFontsPage.java, Class: BashColorsAndFontsPage
 * Last modified: 2013-01-25
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

package com.ansorgit.plugins.bash.editor.highlighting;

import com.ansorgit.plugins.bash.util.BashIcons;
import com.google.common.collect.Maps;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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

    private static final AttributesDescriptor[] ATTRS =
            new AttributesDescriptor[]{
                    new AttributesDescriptor("Line comment", BashSyntaxHighlighter.LINE_COMMENT),
                    new AttributesDescriptor("Shebang (#!) comment", BashSyntaxHighlighter.SHEBANG_COMMENT),
                    new AttributesDescriptor("Keyword", BashSyntaxHighlighter.KEYWORD),
                    new AttributesDescriptor("Parenthesis", BashSyntaxHighlighter.PAREN),
                    new AttributesDescriptor("Braces", BashSyntaxHighlighter.BRACE),
                    new AttributesDescriptor("Brackets", BashSyntaxHighlighter.BRACKET),
                    new AttributesDescriptor("String \"...\"", BashSyntaxHighlighter.STRING),//psi
                    new AttributesDescriptor("String '...'", BashSyntaxHighlighter.STRING2),
                    new AttributesDescriptor("Number", BashSyntaxHighlighter.NUMBER),
                    new AttributesDescriptor("Command redirection", BashSyntaxHighlighter.REDIRECTION),
                    new AttributesDescriptor("Conditional operator", BashSyntaxHighlighter.CONDITIONAL),

                    new AttributesDescriptor("Function definition", BashSyntaxHighlighter.FUNCTION_DEF_NAME),

                    new AttributesDescriptor("Function call", BashSyntaxHighlighter.FUNCTION_CALL),
                    new AttributesDescriptor("Bash internal command", BashSyntaxHighlighter.INTERNAL_COMMAND),
                    new AttributesDescriptor("External command", BashSyntaxHighlighter.EXTERNAL_COMMAND),
                    new AttributesDescriptor("Subshell command", BashSyntaxHighlighter.SUBSHELL_COMMAND),
                    new AttributesDescriptor("Backquote command `...`", BashSyntaxHighlighter.BACKQUOTE),

                    new AttributesDescriptor("Variable declaration, e.g. a=1", BashSyntaxHighlighter.VAR_DEF),

                    new AttributesDescriptor("Simple variable use", BashSyntaxHighlighter.VAR_USE),
                    new AttributesDescriptor("Use of built-in variable", BashSyntaxHighlighter.VAR_USE_BUILTIN),//psi
                    new AttributesDescriptor("Use of composed variable", BashSyntaxHighlighter.VAR_USE_COMPOSED),//psi

                    //PSI highlighting
                    new AttributesDescriptor("Here-document", BashSyntaxHighlighter.HERE_DOC),
                    new AttributesDescriptor("Here-document start marker", BashSyntaxHighlighter.HERE_DOC_START),
                    new AttributesDescriptor("Here-document end marker", BashSyntaxHighlighter.HERE_DOC_END),

                    new AttributesDescriptor("Bad character", BashSyntaxHighlighter.BAD_CHARACTER),
            };

    @NotNull
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRS;
    }

    @NotNull
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    public SyntaxHighlighter getHighlighter() {
        return new BashSyntaxHighlighter();
    }

    @NonNls
    @NotNull
    public String getDemoText() {
        return "#!/bin/sh\n\n" +
                "<internalCmd>export</internalCmd> <varDef>subject</varDef>=world\n" +
                "echo \"Hello $<internalVar>subject</internalVar>\"\n" +
                "\n" +
                "# Function to greet someone\n" +
                "function <functionDef>greetingTo</functionDef>() {\n" +
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
                "${<internalVar>subject</internalVar>:1:2}\n" +
                "\n" +
                "Unix[0]='Debian'\n" +
                "Unix[1]='Red hat'\n" +
                "Unix[2]='Ubuntu'\n" +
                "Unix[3]='Suse'\n" +
                "\n" +
                "echo ${Unix[1]}";
    }


    private static final Map<String, TextAttributesKey> tags = Maps.newHashMap();

    static {
        tags.put("string", BashSyntaxHighlighter.STRING);
        tags.put("heredoc", BashSyntaxHighlighter.HERE_DOC);
        tags.put("heredocStart", BashSyntaxHighlighter.HERE_DOC_START);
        tags.put("heredocEnd", BashSyntaxHighlighter.HERE_DOC_END);
        tags.put("backquote", BashSyntaxHighlighter.BACKQUOTE);
        tags.put("internalCmd", BashSyntaxHighlighter.INTERNAL_COMMAND);
        tags.put("externalCmd", BashSyntaxHighlighter.EXTERNAL_COMMAND);
        tags.put("subshellCmd", BashSyntaxHighlighter.SUBSHELL_COMMAND);
        tags.put("functionDef", BashSyntaxHighlighter.FUNCTION_DEF_NAME);
        tags.put("functionCall", BashSyntaxHighlighter.FUNCTION_CALL);
        tags.put("varDef", BashSyntaxHighlighter.VAR_DEF);
        tags.put("internalVar", BashSyntaxHighlighter.VAR_USE_BUILTIN);
        tags.put("composedVar", BashSyntaxHighlighter.VAR_USE_COMPOSED);

        // we need this to be able to insert << in the text
        // (relies on the current implementation of JetBrain's HighlightsExtractor class)
        tags.put("dummy", TextAttributesKey.find("dummy"));
    }

    @Nullable
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return tags;
    }
}
