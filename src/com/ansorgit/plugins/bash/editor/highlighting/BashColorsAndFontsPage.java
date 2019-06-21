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

package com.ansorgit.plugins.bash.editor.highlighting;

import com.ansorgit.plugins.bash.util.BashIcons;
import com.google.common.collect.Maps;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.io.StreamUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
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
                    new AttributesDescriptor("Binary data", BashSyntaxHighlighter.BINARY_DATA),

                    new AttributesDescriptor("Line comment", BashSyntaxHighlighter.LINE_COMMENT),
                    new AttributesDescriptor("Shebang (#!) comment", BashSyntaxHighlighter.SHEBANG_COMMENT),

                    new AttributesDescriptor("Keyword", BashSyntaxHighlighter.KEYWORD),

                    new AttributesDescriptor("Parenthesis", BashSyntaxHighlighter.PAREN),
                    new AttributesDescriptor("Braces", BashSyntaxHighlighter.BRACE),
                    new AttributesDescriptor("Brackets", BashSyntaxHighlighter.BRACKET),

                    new AttributesDescriptor("String \"...\"", BashSyntaxHighlighter.STRING),//psi
                    new AttributesDescriptor("String '...'", BashSyntaxHighlighter.STRING2),
                    new AttributesDescriptor("Number", BashSyntaxHighlighter.NUMBER),

                    new AttributesDescriptor("Backquotes `...`", BashSyntaxHighlighter.BACKQUOTE),

                    new AttributesDescriptor("Command redirection", BashSyntaxHighlighter.REDIRECTION),
                    new AttributesDescriptor("Conditional operator", BashSyntaxHighlighter.CONDITIONAL),

                    new AttributesDescriptor("Function definition", BashSyntaxHighlighter.FUNCTION_DEF_NAME),

                    new AttributesDescriptor("Function call", BashSyntaxHighlighter.FUNCTION_CALL),
                    new AttributesDescriptor("Bash internal command", BashSyntaxHighlighter.INTERNAL_COMMAND),
                    new AttributesDescriptor("External command", BashSyntaxHighlighter.EXTERNAL_COMMAND),

                    new AttributesDescriptor("Variable declaration, e.g. a=1", BashSyntaxHighlighter.VAR_DEF),

                    new AttributesDescriptor("Simple variable use", BashSyntaxHighlighter.VAR_USE),
                    new AttributesDescriptor("Use of built-in variable", BashSyntaxHighlighter.VAR_USE_BUILTIN),//psi
                    new AttributesDescriptor("Use of composed variable", BashSyntaxHighlighter.VAR_USE_COMPOSED),//psi

                    //PSI highlighting
                    new AttributesDescriptor("Here-document", BashSyntaxHighlighter.HERE_DOC),
                    new AttributesDescriptor("Here-document start marker", BashSyntaxHighlighter.HERE_DOC_START),
                    new AttributesDescriptor("Here-document end marker", BashSyntaxHighlighter.HERE_DOC_END),
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
        InputStream resource = getClass().getClassLoader().getResourceAsStream("/highlighterDemoText.sh");
        String demoText;
        try {
            demoText = StreamUtil.readText(resource, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("BashSupport could not load the syntax highlighter demo text.", e);
        }

        return demoText;
    }

    private static final Map<String, TextAttributesKey> tags = Maps.newHashMap();

    static {
        tags.put("keyword", BashSyntaxHighlighter.KEYWORD);

        tags.put("binary", BashSyntaxHighlighter.BINARY_DATA);

        tags.put("shebang", BashSyntaxHighlighter.SHEBANG_COMMENT);
        tags.put("lineComment", BashSyntaxHighlighter.LINE_COMMENT);
        tags.put("number", BashSyntaxHighlighter.NUMBER);

        tags.put("redirect", BashSyntaxHighlighter.REDIRECTION);

        tags.put("string", BashSyntaxHighlighter.STRING);
        tags.put("simpleString", BashSyntaxHighlighter.STRING2);

        tags.put("heredoc", BashSyntaxHighlighter.HERE_DOC);
        tags.put("heredocStart", BashSyntaxHighlighter.HERE_DOC_START);
        tags.put("heredocEnd", BashSyntaxHighlighter.HERE_DOC_END);

        tags.put("backquote", BashSyntaxHighlighter.BACKQUOTE);

        tags.put("internalCmd", BashSyntaxHighlighter.INTERNAL_COMMAND);
        tags.put("externalCmd", BashSyntaxHighlighter.EXTERNAL_COMMAND);

        tags.put("functionDef", BashSyntaxHighlighter.FUNCTION_DEF_NAME);
        tags.put("functionCall", BashSyntaxHighlighter.FUNCTION_CALL);

        tags.put("varDef", BashSyntaxHighlighter.VAR_DEF);
        tags.put("varUse", BashSyntaxHighlighter.VAR_USE);
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
