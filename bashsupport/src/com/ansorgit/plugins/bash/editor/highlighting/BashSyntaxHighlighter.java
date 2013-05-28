/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSyntaxHighlighter.java, Class: BashSyntaxHighlighter
 * Last modified: 2013-05-02
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

import com.ansorgit.plugins.bash.lang.lexer.BashLexer;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.google.common.collect.Maps;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Map;

import static com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes.conditionalOperators;
import static com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes.redirectionSet;

/**
 * Defines bash token highlighting and formatting.
 */
public class BashSyntaxHighlighter extends SyntaxHighlighterBase {
    //keys
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("BASH.KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey("BASH.LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey SHEBANG_COMMENT = TextAttributesKey.createTextAttributesKey("BASH.SHEBANG", LINE_COMMENT);

    public static final TextAttributesKey PAREN = TextAttributesKey.createTextAttributesKey("BASH.PAREN", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey BRACE = TextAttributesKey.createTextAttributesKey("BASH.BRACE", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey BRACKET = TextAttributesKey.createTextAttributesKey("BASH.BRACKET", DefaultLanguageHighlighterColors.PARENTHESES);

    public static final TextAttributesKey STRING2 = TextAttributesKey.createTextAttributesKey("BASH.STRING2", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("BASH.NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey REDIRECTION = TextAttributesKey.createTextAttributesKey("BASH.REDIRECTION", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey CONDITIONAL = TextAttributesKey.createTextAttributesKey("BASH.CONDITIONAL", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey("BASH.BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    //psi highlighting
    public static final TextAttributesKey TEXT = HighlighterColors.TEXT;

    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("BASH.STRING", STRING2);
    public static final TextAttributesKey BACKQUOTE = TextAttributesKey.createTextAttributesKey("BASH.BACKQUOTE");

    public static final TextAttributesKey HERE_DOC = TextAttributesKey.createTextAttributesKey("BASH.HERE_DOC", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey HERE_DOC_START = TextAttributesKey.createTextAttributesKey("BASH.HERE_DOC_START", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey HERE_DOC_END = TextAttributesKey.createTextAttributesKey("BASH.HERE_DOC_END", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey INTERNAL_COMMAND = TextAttributesKey.createTextAttributesKey("BASH.INTERNAL_COMMAND", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    public static final TextAttributesKey EXTERNAL_COMMAND = TextAttributesKey.createTextAttributesKey("BASH.EXTERNAL_COMMAND", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    public static final TextAttributesKey SUBSHELL_COMMAND = TextAttributesKey.createTextAttributesKey("BASH.SUBSHELL_COMMAND", DefaultLanguageHighlighterColors.FUNCTION_CALL);

    public static final TextAttributesKey FUNCTION_DEF_NAME = TextAttributesKey.createTextAttributesKey("BASH.FUNCTION_DEF_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey FUNCTION_CALL = TextAttributesKey.createTextAttributesKey("BASH.FUNCTION_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL);

    public static final TextAttributesKey VAR_USE = TextAttributesKey.createTextAttributesKey("BASH.VAR_USE", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE);

    public static final TextAttributesKey VAR_DEF = TextAttributesKey.createTextAttributesKey("BASH.VAR_DEF", VAR_USE);

    public static final TextAttributesKey VAR_USE_BUILTIN = TextAttributesKey.createTextAttributesKey("BASH.VAR_USE_BUILTIN", VAR_USE);
    public static final TextAttributesKey VAR_USE_COMPOSED = TextAttributesKey.createTextAttributesKey("BASH.VAR_USE_COMPOSED", VAR_USE);

    public static final TextAttributesKey NONE = HighlighterColors.TEXT;

    private static final Map<IElementType, TextAttributesKey> attributes1 = Maps.newHashMap();
    private static final Map<IElementType, TextAttributesKey> attributes2 = Maps.newHashMap();

    @NotNull
    public Lexer getHighlightingLexer() {
        return new BashLexer();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(final IElementType tokenType) {
        return pack(attributes1.get(tokenType), attributes2.get(tokenType));
    }
}


